import { pgTable, serial, text, integer, boolean, doublePrecision, bigint, primaryKey } from "drizzle-orm/pg-core";

export const users = pgTable("users", {
  id: serial("id").primaryKey(),
  username: text("username").notNull().unique(),
  passwordHash: text("password_hash").notNull(),
});

export const reports = pgTable("reports", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull().references(() => users.id, { onDelete: "cascade" }),
  username: text("username").notNull(),
  title: text("title").notNull(),
  description: text("description").notNull(),
  location: text("location").notNull(),
  mediaUri: text("media_uri"),
  isVideo: boolean("is_video").default(false).notNull(),
  latitude: doublePrecision("latitude"),
  longitude: doublePrecision("longitude"),
  timestamp: bigint("timestamp", { mode: "number" }).notNull(),
});

export const progressUpdates = pgTable("progress_updates", {
  id: serial("id").primaryKey(),
  reportId: integer("report_id").notNull().references(() => reports.id, { onDelete: "cascade" }),
  username: text("username").notNull(),
  text: text("text").notNull(),
  mediaUri: text("media_uri"),
  isVideo: boolean("is_video").default(false).notNull(),
  timestamp: bigint("timestamp", { mode: "number" }).notNull(),
});

export const messages = pgTable("messages", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull(),
  username: text("username").notNull(),
  text: text("text").notNull(),
  timestamp: bigint("timestamp", { mode: "number" }).notNull(),
});

export const follows = pgTable("follows", {
  followerId: integer("follower_id").notNull().references(() => users.id, { onDelete: "cascade" }),
  followedId: integer("followed_id").notNull().references(() => users.id, { onDelete: "cascade" }),
}, (table) => {
  return {
    pk: primaryKey({ columns: [table.followerId, table.followedId] }),
  };
});
