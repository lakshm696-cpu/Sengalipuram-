import type { Config } from "@netlify/functions";
import { db } from "../../db/index.js";
import { users, reports, progressUpdates, messages, follows } from "../../db/schema.js";
import { eq, and, desc } from "drizzle-orm";
import { GoogleGenAI } from "@google/genai";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
};

function jsonResponse(data: any, status = 200) {
  return Response.json(data, {
    status,
    headers: corsHeaders,
  });
}

export default async (req: Request) => {
  const method = req.method;
  
  if (method === "OPTIONS") {
    return new Response(null, { headers: corsHeaders });
  }

  const url = new URL(req.url);
  const path = url.pathname;

  try {
    // 1. POST /api/auth/register
    if (path === "/api/auth/register" && method === "POST") {
      const { username, passwordHash } = await req.json();
      if (!username || !passwordHash) {
        return jsonResponse({ error: "Username and password are required" }, 400);
      }

      // Check if user already exists
      const existingUser = await db.select().from(users).where(eq(users.username, username)).limit(1);
      if (existingUser.length > 0) {
        return jsonResponse({ error: "Username already exists" }, 400);
      }

      const [newUser] = await db.insert(users).values({ username, passwordHash }).returning();
      return jsonResponse(newUser, 201);
    }

    // 2. POST /api/auth/login
    if (path === "/api/auth/login" && method === "POST") {
      const { username, passwordHash } = await req.json();
      if (!username || !passwordHash) {
        return jsonResponse({ error: "Username and password are required" }, 400);
      }

      const [user] = await db.select().from(users).where(eq(users.username, username)).limit(1);
      if (!user || user.passwordHash !== passwordHash) {
        return jsonResponse({ error: "Invalid username or password" }, 401);
      }

      return jsonResponse(user, 200);
    }

    // 3. GET & POST /api/reports
    if (path === "/api/reports") {
      if (method === "GET") {
        const allReports = await db.select().from(reports).orderBy(desc(reports.timestamp));
        return jsonResponse(allReports, 200);
      }

      if (method === "POST") {
        const body = await req.json();
        const { userId, username, title, description, location, mediaUri, isVideo, latitude, longitude, timestamp } = body;
        
        if (!userId || !username || !title || !description) {
          return jsonResponse({ error: "Missing required report fields" }, 400);
        }

        const [newReport] = await db.insert(reports).values({
          userId,
          username,
          title,
          description,
          location: location || "",
          mediaUri,
          isVideo: isVideo || false,
          latitude: latitude !== undefined ? latitude : null,
          longitude: longitude !== undefined ? longitude : null,
          timestamp: timestamp || Date.now()
        }).returning();

        return jsonResponse(newReport, 201);
      }
    }

    // 4. DELETE /api/reports/:id
    const reportMatch = path.match(/^\/api\/reports\/(\d+)$/);
    if (reportMatch && method === "DELETE") {
      const reportId = parseInt(reportMatch[1], 10);
      await db.delete(reports).where(eq(reports.id, reportId));
      return jsonResponse({ success: true, message: `Report ${reportId} deleted` }, 200);
    }

    // 5. GET & POST /api/reports/:id/updates
    const updatesMatch = path.match(/^\/api\/reports\/(\d+)\/updates$/);
    if (updatesMatch) {
      const reportId = parseInt(updatesMatch[1], 10);

      if (method === "GET") {
        const updates = await db.select().from(progressUpdates).where(eq(progressUpdates.reportId, reportId)).orderBy(desc(progressUpdates.timestamp));
        return jsonResponse(updates, 200);
      }

      if (method === "POST") {
        const { username, text, mediaUri, isVideo, timestamp } = await req.json();
        if (!username || !text) {
          return jsonResponse({ error: "Username and text are required" }, 400);
        }

        const [newUpdate] = await db.insert(progressUpdates).values({
          reportId,
          username,
          text,
          mediaUri,
          isVideo: isVideo || false,
          timestamp: timestamp || Date.now()
        }).returning();

        return jsonResponse(newUpdate, 201);
      }
    }

    // 6. DELETE /api/updates/:id
    const deleteUpdateMatch = path.match(/^\/api\/updates\/(\d+)$/);
    if (deleteUpdateMatch && method === "DELETE") {
      const updateId = parseInt(deleteUpdateMatch[1], 10);
      await db.delete(progressUpdates).where(eq(progressUpdates.id, updateId));
      return jsonResponse({ success: true, message: `Progress update ${updateId} deleted` }, 200);
    }

    // 7. GET & POST /api/messages
    if (path === "/api/messages") {
      if (method === "GET") {
        const allMessages = await db.select().from(messages).orderBy(desc(messages.timestamp));
        return jsonResponse(allMessages, 200);
      }

      if (method === "POST") {
        const { userId, username, text, timestamp } = await req.json();
        if (!userId || !username || !text) {
          return jsonResponse({ error: "Missing required message fields" }, 400);
        }

        const [newMessage] = await db.insert(messages).values({
          userId,
          username,
          text,
          timestamp: timestamp || Date.now()
        }).returning();

        return jsonResponse(newMessage, 201);
      }
    }

    // 8. GET /api/users/:id/follows
    const userFollowsMatch = path.match(/^\/api\/users\/(\d+)\/follows$/);
    if (userFollowsMatch && method === "GET") {
      const userId = parseInt(userFollowsMatch[1], 10);
      const userFollows = await db.select({ followedId: follows.followedId }).from(follows).where(eq(follows.followerId, userId));
      const followedIds = userFollows.map(f => f.followedId);
      return jsonResponse(followedIds, 200);
    }

    // 9. POST /api/follows
    if (path === "/api/follows" && method === "POST") {
      const { followerId, followedId } = await req.json();
      if (!followerId || !followedId) {
        return jsonResponse({ error: "followerId and followedId are required" }, 400);
      }

      await db.insert(follows).values({ followerId, followedId }).onConflictDoNothing();
      return jsonResponse({ success: true }, 201);
    }

    // 10. POST /api/follows/unfollow
    if (path === "/api/follows/unfollow" && method === "POST") {
      const { followerId, followedId } = await req.json();
      if (!followerId || !followedId) {
        return jsonResponse({ error: "followerId and followedId are required" }, 400);
      }

      await db.delete(follows).where(and(eq(follows.followerId, followerId), eq(follows.followedId, followedId)));
      return jsonResponse({ success: true }, 200);
    }

    // 11. POST /api/reports/analyze-priority (Gemini AI Endpoint)
    if (path === "/api/reports/analyze-priority" && method === "POST") {
      const { title, description } = await req.json();
      if (!title || !description) {
        return jsonResponse({ error: "Title and description are required for AI analysis" }, 400);
      }

      try {
        const ai = new GoogleGenAI({});
        const prompt = `Analyze this village improvement report and categorize it into one of these: [Roads, Water, Electricity, Health, Sanitation, Public Spaces, Education, Others]. Also estimate its priority as [Low, Medium, High] depending on the urgency of the problem described (e.g. broken bridge or no drinking water is High, minor painting or general cleanup is Low/Medium). Provide the result in raw JSON format with fields: "priority", "category", "explanation" (a brief 1-2 sentence explanation of why this priority/category was selected).
        Title: ${title}
        Description: ${description}`;

        const aiResponse = await ai.models.generateContent({
          model: "gemini-3-flash-preview",
          contents: prompt,
        });

        const text = aiResponse.text;
        const jsonMatch = text?.match(/\{[\s\S]*\}/);
        const resultJson = jsonMatch ? JSON.parse(jsonMatch[0]) : { priority: "Medium", category: "Others", explanation: text };
        
        return jsonResponse(resultJson, 200);
      } catch (err: any) {
        return jsonResponse({
          error: "AI Analysis failed",
          details: err.message,
          priority: "Medium",
          category: "Others",
          explanation: "Analysis unavailable due to server-side AI model timeout."
        }, 200); // Return 200 with fallback values so client doesn't crash
      }
    }

    return jsonResponse({ error: `Not found: ${method} ${path}` }, 404);

  } catch (error: any) {
    return jsonResponse({ error: "Internal Server Error", details: error.message }, 500);
  }
};

export const config: Config = {
  path: "/api/*",
};
