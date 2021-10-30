ALTER TABLE public.user_token
  ADD COLUMN IF NOT EXISTS expiration_date timestamp;