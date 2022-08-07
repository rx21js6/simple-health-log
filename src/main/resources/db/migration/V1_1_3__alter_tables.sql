ALTER TABLE public.user_info
  ADD COLUMN IF NOT EXISTS security_level integer NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS encrypted_name text,
  ADD COLUMN IF NOT EXISTS encrypted_mail_address text;
