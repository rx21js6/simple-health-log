ALTER TABLE public.user_info
  ADD COLUMN IF NOT EXISTS zone_id text NOT NULL DEFAULT '';
