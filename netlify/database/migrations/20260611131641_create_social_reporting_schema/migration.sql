CREATE TABLE "follows" (
	"follower_id" integer,
	"followed_id" integer,
	CONSTRAINT "follows_pkey" PRIMARY KEY("follower_id","followed_id")
);
--> statement-breakpoint
CREATE TABLE "messages" (
	"id" serial PRIMARY KEY,
	"user_id" integer NOT NULL,
	"username" text NOT NULL,
	"text" text NOT NULL,
	"timestamp" bigint NOT NULL
);
--> statement-breakpoint
CREATE TABLE "progress_updates" (
	"id" serial PRIMARY KEY,
	"report_id" integer NOT NULL,
	"username" text NOT NULL,
	"text" text NOT NULL,
	"media_uri" text,
	"is_video" boolean DEFAULT false NOT NULL,
	"timestamp" bigint NOT NULL
);
--> statement-breakpoint
CREATE TABLE "reports" (
	"id" serial PRIMARY KEY,
	"user_id" integer NOT NULL,
	"username" text NOT NULL,
	"title" text NOT NULL,
	"description" text NOT NULL,
	"location" text NOT NULL,
	"media_uri" text,
	"is_video" boolean DEFAULT false NOT NULL,
	"latitude" double precision,
	"longitude" double precision,
	"timestamp" bigint NOT NULL
);
--> statement-breakpoint
CREATE TABLE "users" (
	"id" serial PRIMARY KEY,
	"username" text NOT NULL UNIQUE,
	"password_hash" text NOT NULL
);
--> statement-breakpoint
ALTER TABLE "follows" ADD CONSTRAINT "follows_follower_id_users_id_fkey" FOREIGN KEY ("follower_id") REFERENCES "users"("id") ON DELETE CASCADE;--> statement-breakpoint
ALTER TABLE "follows" ADD CONSTRAINT "follows_followed_id_users_id_fkey" FOREIGN KEY ("followed_id") REFERENCES "users"("id") ON DELETE CASCADE;--> statement-breakpoint
ALTER TABLE "progress_updates" ADD CONSTRAINT "progress_updates_report_id_reports_id_fkey" FOREIGN KEY ("report_id") REFERENCES "reports"("id") ON DELETE CASCADE;--> statement-breakpoint
ALTER TABLE "reports" ADD CONSTRAINT "reports_user_id_users_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE;