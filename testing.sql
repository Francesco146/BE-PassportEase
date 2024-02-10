--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4 (Debian 15.4-1.pgdg120+1)
-- Dumped by pg_dump version 15.4 (Debian 15.4-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: availabilities; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.availabilities
(
    id         uuid NOT NULL,
    created_at timestamp(6) without time zone,
    date       timestamp(6) without time zone,
    status     character varying(255),
    "time"     time(6) without time zone,
    updated_at timestamp(6) without time zone,
    office_id  uuid,
    request_id uuid,
    user_id    uuid,
    CONSTRAINT availabilities_status_check CHECK (((status)::text = ANY
                                                   (ARRAY [('FREE'::character varying)::text, ('TAKEN'::character varying)::text, ('TIMEDOUT'::character varying)::text, ('CANCELLED'::character varying)::text, ('PENDING'::character varying)::text])))
);


ALTER TABLE public.availabilities
    OWNER TO myuser;

--
-- Name: categories; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.categories
(
    id          uuid NOT NULL,
    created_at  timestamp(6) without time zone,
    description character varying(255),
    name        character varying(255),
    updated_at  timestamp(6) without time zone
);


ALTER TABLE public.categories
    OWNER TO myuser;

--
-- Name: citizens; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.citizens
(
    id                 uuid NOT NULL,
    city_of_birth      character varying(255),
    created_at         timestamp(6) without time zone,
    date_of_birth      timestamp(6) without time zone,
    fiscal_code        character varying(255),
    health_card_number character varying(255),
    name               character varying(255),
    surname            character varying(255),
    updated_at         timestamp(6) without time zone
);


ALTER TABLE public.citizens
    OWNER TO myuser;

--
-- Name: citizens_categories; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.citizens_categories
(
    id          uuid NOT NULL,
    created_at  timestamp(6) without time zone,
    updated_at  timestamp(6) without time zone,
    category_id uuid,
    citizen_id  uuid
);


ALTER TABLE public.citizens_categories
    OWNER TO myuser;

--
-- Name: notifications; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.notifications
(
    id              uuid NOT NULL,
    created_at      timestamp(6) without time zone,
    end_date        timestamp(6) without time zone,
    is_ready        boolean,
    message         character varying(255),
    start_date      timestamp(6) without time zone,
    updated_at      timestamp(6) without time zone,
    office_id       uuid,
    request_type_id uuid,
    user_id         uuid
);


ALTER TABLE public.notifications
    OWNER TO myuser;

--
-- Name: office_working_days; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.office_working_days
(
    id          uuid NOT NULL,
    created_at  timestamp(6) without time zone,
    day         character varying(255),
    end_time1   time(6) without time zone,
    end_time2   time(6) without time zone,
    start_time1 time(6) without time zone,
    start_time2 time(6) without time zone,
    updated_at  timestamp(6) without time zone,
    office_id   uuid,
    CONSTRAINT office_working_days_day_check CHECK (((day)::text = ANY
                                                     (ARRAY [('SUNDAY'::character varying)::text, ('MONDAY'::character varying)::text, ('TUESDAY'::character varying)::text, ('WEDNESDAY'::character varying)::text, ('THURSDAY'::character varying)::text, ('FRIDAY'::character varying)::text, ('SATURDAY'::character varying)::text])))
);


ALTER TABLE public.office_working_days
    OWNER TO myuser;

--
-- Name: offices; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.offices
(
    id         uuid NOT NULL,
    address    character varying(255),
    created_at timestamp(6) without time zone,
    name       character varying(255),
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.offices
    OWNER TO myuser;

--
-- Name: request_types; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.request_types
(
    id             uuid NOT NULL,
    created_at     timestamp(6) without time zone,
    name           character varying(255),
    updated_at     timestamp(6) without time zone,
    has_dependency boolean
);


ALTER TABLE public.request_types
    OWNER TO myuser;

--
-- Name: requests; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.requests
(
    id              uuid   NOT NULL,
    created_at      timestamp(6) without time zone,
    duration        bigint NOT NULL,
    end_date        timestamp(6) without time zone,
    end_time        time(6) without time zone,
    start_date      timestamp(6) without time zone,
    start_time      time(6) without time zone,
    updated_at      timestamp(6) without time zone,
    request_type_id uuid,
    worker_id       uuid
);


ALTER TABLE public.requests
    OWNER TO myuser;

--
-- Name: requests_offices; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.requests_offices
(
    id         uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    office_id  uuid,
    request_id uuid
);


ALTER TABLE public.requests_offices
    OWNER TO myuser;

--
-- Name: users; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.users
(
    id            uuid NOT NULL,
    active        boolean,
    city_of_birth character varying(255),
    created_at    timestamp(6) without time zone,
    date_of_birth timestamp(6) without time zone,
    email         character varying(255),
    fiscal_code   character varying(255),
    hash_password character varying(255),
    name          character varying(255),
    refresh_token character varying(1024),
    surname       character varying(255),
    updated_at    timestamp(6) without time zone
);


ALTER TABLE public.users
    OWNER TO myuser;

--
-- Name: workers; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.workers
(
    id            uuid NOT NULL,
    created_at    timestamp(6) without time zone,
    email         character varying(255),
    hash_password character varying(255),
    refresh_token character varying(1024),
    updated_at    timestamp(6) without time zone,
    username      character varying(255),
    office_id     uuid
);


ALTER TABLE public.workers
    OWNER TO myuser;

--
-- Data for Name: availabilities; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.availabilities (id, created_at, date, status, "time", updated_at, office_id, request_id,
                            user_id) FROM stdin;
507d06ec-ff38-4afc-aada-7bd1575fc44b	2023-08-29 14:05:57.509	2023-08-28 00:00:00	FREE	08:00:00	2023-08-29 14:05:57.509	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
fa1a3e6f-dc17-4f25-9144-f3ba7ef187e7	2023-08-29 14:05:57.516	2023-08-28 00:00:00	FREE	09:00:00	2023-08-29 14:05:57.516	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
4a65bd04-6d52-4816-806d-6d13278ff019	2023-08-29 14:05:57.52	2023-08-28 00:00:00	FREE	11:00:00	2023-08-29 14:05:57.52	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
8bcfd8f8-ebc3-4aa2-970b-d4111d94c10d	2023-08-29 14:05:57.522	2023-08-28 00:00:00	FREE	14:00:00	2023-08-29 14:05:57.522	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
010981c8-a081-4a88-962c-749844b459a1	2023-08-29 14:05:57.524	2023-08-28 00:00:00	FREE	15:00:00	2023-08-29 14:05:57.524	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
3d29583c-b3a4-49c7-9b49-bbd1abc8df78	2023-08-29 14:05:57.526	2023-08-28 00:00:00	FREE	16:00:00	2023-08-29 14:05:57.526	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
737d4945-05a1-4ef7-ac10-893d5c3140bf	2023-08-29 14:05:57.527	2023-08-28 00:00:00	FREE	17:00:00	2023-08-29 14:05:57.527	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	\N
c3030ccf-67e0-4d58-a7d0-554a16a0fad6	2023-08-29 14:05:57.518	2023-08-28 00:00:00	TAKEN	10:00:00	2023-08-29 14:05:57.518	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7	320eb378-47ed-4316-87ce-1f344993e4dc
02bf75b5-63b3-4138-9825-5c01cc40e8e9	2023-08-29 14:09:43.669	2023-08-28 00:00:00	FREE	08:00:00	2023-08-29 14:09:43.669	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
3bbb1d6c-b973-43b7-8b84-86ef5d537742	2023-08-29 14:09:43.672	2023-08-28 00:00:00	FREE	09:00:00	2023-08-29 14:09:43.672	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
fcef44cc-5b17-4848-aa46-7c731e3ce431	2023-08-29 14:09:43.675	2023-08-28 00:00:00	FREE	10:00:00	2023-08-29 14:09:43.675	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
469490a8-6df0-42ee-9cc8-8616284099c4	2023-08-29 14:09:43.677	2023-08-28 00:00:00	FREE	11:00:00	2023-08-29 14:09:43.677	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
6aeafe3e-25e1-4a2e-9b65-db2de9ce2ff8	2023-08-29 14:09:43.681	2023-08-28 00:00:00	FREE	14:00:00	2023-08-29 14:09:43.681	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
1d6dd79f-3c31-4868-ba69-d2e1b1d8d864	2023-08-29 14:09:43.683	2023-08-28 00:00:00	FREE	15:00:00	2023-08-29 14:09:43.683	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
ee19d05a-d533-419c-b48b-9eacd9c0d0e2	2023-08-29 14:09:43.686	2023-08-28 00:00:00	FREE	16:00:00	2023-08-29 14:09:43.686	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
283ff6d3-db3b-4008-ac1d-93b0bfd6cad5	2023-08-29 14:09:43.69	2023-08-28 00:00:00	FREE	17:00:00	2023-08-29 14:09:43.69	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d	\N
1a349e79-95d1-49d8-8a25-c9d2a61cbc2d	2023-08-29 14:13:01.023	2023-08-28 00:00:00	FREE	08:00:00	2023-08-29 14:13:01.023	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
cc571710-5f2a-4887-8960-e0330ae654e5	2023-08-29 14:13:01.024	2023-08-28 00:00:00	FREE	09:00:00	2023-08-29 14:13:01.024	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
b694c3a7-1baa-4ca7-8561-038d54d492cf	2023-08-29 14:13:01.026	2023-08-28 00:00:00	FREE	10:00:00	2023-08-29 14:13:01.026	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
cca50cd5-5c74-4f17-8354-f43a8ec9afb2	2023-08-29 14:13:01.028	2023-08-28 00:00:00	FREE	11:00:00	2023-08-29 14:13:01.028	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
e52a1697-c35b-41d4-b4ff-074527cf450e	2023-08-29 14:13:01.031	2023-08-28 00:00:00	FREE	14:00:00	2023-08-29 14:13:01.031	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
28d4dd6e-ac76-4b66-a7db-cde344b6fa17	2023-08-29 14:13:01.032	2023-08-28 00:00:00	FREE	15:00:00	2023-08-29 14:13:01.032	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
41d55605-56c7-4860-824e-ea34f95cf17b	2023-08-29 14:13:01.034	2023-08-28 00:00:00	FREE	16:00:00	2023-08-29 14:13:01.034	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
696d99d8-968e-4784-a09a-8a884f92f374	2023-08-29 14:13:01.037	2023-08-28 00:00:00	FREE	17:00:00	2023-08-29 14:13:01.037	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90	\N
c6d892e5-007d-4e7d-94fb-a9638a1de7fa	2023-08-29 14:17:28.668	2023-08-29 00:00:00	FREE	08:00:00	2023-08-29 14:17:28.668	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
f768830f-d27b-4667-9cbd-89b8c1ded3a9	2023-08-29 14:17:28.671	2023-08-29 00:00:00	FREE	09:00:00	2023-08-29 14:17:28.671	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
05b12b22-dc2d-4495-ac1a-a9288c5e8d47	2023-08-29 14:17:28.674	2023-08-29 00:00:00	FREE	10:00:00	2023-08-29 14:17:28.674	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
8c842199-d953-4550-a73c-89b8f7519eda	2023-08-29 14:17:28.676	2023-08-29 00:00:00	FREE	11:00:00	2023-08-29 14:17:28.676	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
36c9a28e-4c0d-4b04-8724-801fbe220da1	2023-08-29 14:17:28.681	2023-08-29 00:00:00	FREE	14:00:00	2023-08-29 14:17:28.681	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
38942343-9555-4cae-af96-e1b4ff388a48	2023-08-29 14:17:28.69	2023-08-29 00:00:00	FREE	15:00:00	2023-08-29 14:17:28.69	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
37e185b3-804f-4dfc-843a-dfcbbdb3b958	2023-08-29 14:17:28.693	2023-08-29 00:00:00	FREE	16:00:00	2023-08-29 14:17:28.693	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	\N
b2dc2cc7-3a0e-4bf3-a747-4c7e72297dff	2023-08-31 14:17:28.696	2023-08-31 00:00:00	TAKEN	17:00:00	2023-08-29 14:17:28.696	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c	320eb378-47ed-4316-87ce-1f344993e4dc
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.categories (id, created_at, description, name, updated_at) FROM stdin;
\.


--
-- Data for Name: citizens; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.citizens (id, city_of_birth, created_at, date_of_birth, fiscal_code, health_card_number, name, surname,
                      updated_at) FROM stdin;
46ae6b32-2fec-47fd-b6c3-dd2187a88491	Sona	2014-03-15 01:00:00	1996-03-15 01:00:00	GYAMMY96C15I826C	123456789	Gay	Mommy	1996-03-15 01:00:00
535506d6-62f3-4fc8-9833-28a09cad3527	Legnago	2014-03-15 01:00:00	1996-03-16 01:00:00	FRRFMM96C16E512O	123456789	Furry	Fummi	1996-03-16 01:00:00
60c6be6d-b916-40c3-b845-b0f0d44d8514	La Spezia	2014-03-15 01:00:00	1996-04-15 01:00:00	BLSCLL96D55E463O	123456789	Balsamo	Capelli	1996-04-15 01:00:00
\.


--
-- Data for Name: citizens_categories; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.citizens_categories (id, created_at, updated_at, category_id, citizen_id) FROM stdin;
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.notifications (id, created_at, end_date, is_ready, message, start_date, updated_at, office_id,
                           request_type_id, user_id) FROM stdin;
0ab61ed0-0607-4ca0-a4a3-ebd46dcd7436	2023-10-19 14:29:40.714	2023-09-01 00:00:00	f	\N	2023-08-05 00:00:00	2023-10-19 14:29:40.714	2a29e04b-51fc-4710-b8fc-f68c34b89707	9a7c0343-25a6-4e13-ad18-a46fcba0169a	320eb378-47ed-4316-87ce-1f344993e4dc
\.


--
-- Data for Name: office_working_days; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.office_working_days (id, created_at, day, end_time1, end_time2, start_time1, start_time2, updated_at,
                                 office_id) FROM stdin;
75fae138-ac9d-4cba-bc70-c20cdc9523dd	1996-03-15 01:00:00	MONDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	9eaeb288-35f0-445c-bde9-ce65f064c868
521eea82-52af-4ed3-a923-34c3afcdeb8d	1996-03-15 01:00:00	TUESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	9eaeb288-35f0-445c-bde9-ce65f064c868
c3952670-7001-43c5-a75f-c6c4c977253d	1996-03-15 01:00:00	WEDNESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	9eaeb288-35f0-445c-bde9-ce65f064c868
96201532-ec12-4982-84aa-d2626f5f5c82	1996-03-15 01:00:00	THURSDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	9eaeb288-35f0-445c-bde9-ce65f064c868
be4c38c4-61db-4d3e-8a99-ec8116ac35cf	1996-03-15 01:00:00	FRIDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	9eaeb288-35f0-445c-bde9-ce65f064c868
42986cf5-03b7-41e5-90ae-0579985485c4	1996-03-15 01:00:00	MONDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	2a29e04b-51fc-4710-b8fc-f68c34b89707
ed6652f6-c84d-456e-924e-4fad28e4e082	1996-03-15 01:00:00	TUESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	2a29e04b-51fc-4710-b8fc-f68c34b89707
d4a3ee13-3cca-4bab-b5fa-5df0ee1bf869	1996-03-15 01:00:00	WEDNESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	2a29e04b-51fc-4710-b8fc-f68c34b89707
bb3fbfd5-f2f1-4258-b8b2-f2758ccde1a5	1996-03-15 01:00:00	WEDNESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	661dd5d7-a63c-44d5-80ab-5ad6fc2d027d
aed7316f-49b6-46b5-bf1f-3cd1f6093c7a	1996-03-15 01:00:00	THURSDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	661dd5d7-a63c-44d5-80ab-5ad6fc2d027d
41f000bf-17a6-4ed1-9184-e8879fa01c89	1996-03-15 01:00:00	FRIDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	661dd5d7-a63c-44d5-80ab-5ad6fc2d027d
c275de98-91b8-4cef-9e1c-c2a5e8d06203	1996-03-15 01:00:00	MONDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	804f926e-f1a3-48ae-86b8-73e7746b668f
55471888-8f10-4a6e-b503-15d0cc36557b	1996-03-15 01:00:00	TUESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	804f926e-f1a3-48ae-86b8-73e7746b668f
b41fb0e7-c374-4b1c-8c23-315f0bb0265a	1996-03-15 01:00:00	WEDNESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	804f926e-f1a3-48ae-86b8-73e7746b668f
440fd9bb-c219-4ff5-b947-43ad7c9fddda	1996-03-15 01:00:00	TUESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	0d7fe86a-d7a2-4581-adb2-b4bfd6fb10fd
a7099e58-aa2f-49f1-83fc-cd1ef88f87df	1996-03-15 01:00:00	WEDNESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	0d7fe86a-d7a2-4581-adb2-b4bfd6fb10fd
7806a783-d746-41e4-a34c-8b5c37fbd2e0	1996-03-15 01:00:00	THURSDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	0d7fe86a-d7a2-4581-adb2-b4bfd6fb10fd
fcc40a12-3624-4ad8-81bf-51feb01d79e7	1996-03-15 01:00:00	TUESDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	04cc59b3-bcfa-447c-a787-0f9c639923a2
3e256e2e-3d13-4e67-9274-1703f1a04428	1996-03-15 01:00:00	THURSDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	04cc59b3-bcfa-447c-a787-0f9c639923a2
71b50c7c-32f9-4d0e-abcc-3a868bafe747	1996-03-15 01:00:00	FRIDAY	12:00:00	18:00:00	08:00:00	14:00:00	2023-03-15 01:00:00	04cc59b3-bcfa-447c-a787-0f9c639923a2
\.


--
-- Data for Name: offices; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.offices (id, address, created_at, name, updated_at) FROM stdin;
9eaeb288-35f0-445c-bde9-ce65f064c868	via Garibaldi	2014-03-15 01:00:00	Sede di Mantova	2023-03-15 01:00:00
2a29e04b-51fc-4710-b8fc-f68c34b89707	via Brombeis	2014-03-15 01:00:00	Sede di Napoli	2023-03-15 01:00:00
661dd5d7-a63c-44d5-80ab-5ad6fc2d027d	via Terra	2014-03-15 01:00:00	Sede di Verona	2023-03-15 01:00:00
804f926e-f1a3-48ae-86b8-73e7746b668f	via Dei Fummi	2014-03-15 01:00:00	Sede di Venezia	2023-03-15 01:00:00
0d7fe86a-d7a2-4581-adb2-b4bfd6fb10fd	via Hot Mommies	2014-03-15 01:00:00	Sede di Latina	2023-03-15 01:00:00
04cc59b3-bcfa-447c-a787-0f9c639923a2	via Delle Scimmie	2014-03-15 01:00:00	Sede di Bari	2023-03-15 01:00:00
\.


--
-- Data for Name: request_types; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.request_types (id, created_at, name, updated_at, has_dependency) FROM stdin;
8ac0d81c-7568-4109-9aff-97f2e2c94244	2023-08-29 14:13:01.009	Ritiro Passaporto	2023-08-29 14:13:01.009	t
063a5392-ea71-4182-ae35-7be5f68797e7	1996-03-15 01:00:00	furto passaporto	1996-03-15 01:00:00	t
c6f6b7fd-4b0c-4085-ae67-4c46c2049e3e	2023-08-29 14:09:43.646	Creazione Passaporto	2023-08-29 14:09:43.646	f
dac90ec6-cca4-4f49-a2be-df0d2d7c482e	2023-08-29 13:58:32.873	Digitalizzazione Passaporto	2023-08-29 13:58:32.873	f
9a7c0343-25a6-4e13-ad18-a46fcba0169a	1996-03-15 01:00:00	ritiro passaporti	1996-03-15 01:00:00	t
9e99083f-3844-41c8-8827-549bd08ace07	1996-03-15 01:00:00	rilascio passaporto prima volta	1996-03-15 01:00:00	t
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.requests (id, created_at, duration, end_date, end_time, start_date, start_time, updated_at, request_type_id,
                      worker_id) FROM stdin;
f97ab954-a635-4b39-a557-fc604f7c5b30	2023-08-29 13:58:32.9	60	2024-08-28 00:00:00	18:00:00	2024-08-28 00:00:00	08:00:00	2023-08-29 13:58:32.9	dac90ec6-cca4-4f49-a2be-df0d2d7c482e	eb1bb88e-4e32-4643-a124-b45fb25f0c12
768a90ef-2a39-4884-928b-e48f43b1e4b7	2023-08-29 14:05:57.491	60	2023-08-29 00:00:00	18:00:00	2023-08-28 00:00:00	08:00:00	2023-08-29 14:05:57.491	dac90ec6-cca4-4f49-a2be-df0d2d7c482e	34a5bc6f-6ccb-4a91-b517-65bf5a53776c
8c79766d-3b60-490b-a37c-b8e9798e6d4d	2023-08-29 14:09:43.656	60	2023-08-29 00:00:00	18:00:00	2023-08-28 00:00:00	08:00:00	2023-08-29 14:09:43.656	c6f6b7fd-4b0c-4085-ae67-4c46c2049e3e	34a5bc6f-6ccb-4a91-b517-65bf5a53776c
84c127e6-5b1a-4914-877a-d95ac9070e90	2023-08-29 14:13:01.015	60	2023-08-29 00:00:00	18:00:00	2023-08-28 00:00:00	08:00:00	2023-08-29 14:13:01.015	8ac0d81c-7568-4109-9aff-97f2e2c94244	34a5bc6f-6ccb-4a91-b517-65bf5a53776c
2b824e96-7146-4f51-a735-3bf2a1c0bf8c	2023-08-29 14:17:28.648	60	2023-08-30 00:00:00	18:00:00	2023-08-29 00:00:00	08:00:00	2023-08-29 14:17:28.648	dac90ec6-cca4-4f49-a2be-df0d2d7c482e	34a5bc6f-6ccb-4a91-b517-65bf5a53776c
a270e152-2b7e-4af7-82be-cb51b489c4b4	2023-08-29 14:20:36.582	60	2023-09-02 00:00:00	18:00:00	2023-09-01 00:00:00	08:00:00	2023-08-29 14:20:36.582	dac90ec6-cca4-4f49-a2be-df0d2d7c482e	eb1bb88e-4e32-4643-a124-b45fb25f0c12
eda98b8c-bb21-4f90-8fe3-d7d122a1dfbc	2023-08-29 14:20:55.814	60	2023-09-02 00:00:00	18:00:00	2023-09-01 00:00:00	08:00:00	2023-08-29 14:20:55.814	dac90ec6-cca4-4f49-a2be-df0d2d7c482e	eb1bb88e-4e32-4643-a124-b45fb25f0c12
d859b0b6-0de7-4652-bf4f-58d995cea871	2023-08-29 14:21:19.669	60	2023-09-02 00:00:00	18:00:00	2023-09-01 00:00:00	08:00:00	2023-08-29 14:21:19.669	c6f6b7fd-4b0c-4085-ae67-4c46c2049e3e	eb1bb88e-4e32-4643-a124-b45fb25f0c12
\.


--
-- Data for Name: requests_offices; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.requests_offices (id, created_at, updated_at, office_id, request_id) FROM stdin;
f64ab24c-4753-498c-a07c-3c984b9fa22e	2023-08-29 13:58:32.912	2023-08-29 13:58:32.912	0d7fe86a-d7a2-4581-adb2-b4bfd6fb10fd	f97ab954-a635-4b39-a557-fc604f7c5b30
605a0e22-e121-4d0f-b090-cbed740ce2b7	2023-08-29 14:05:57.497	2023-08-29 14:05:57.497	2a29e04b-51fc-4710-b8fc-f68c34b89707	768a90ef-2a39-4884-928b-e48f43b1e4b7
621b84fd-743a-4287-b271-58ad4a4ae534	2023-08-29 14:09:43.659	2023-08-29 14:09:43.659	2a29e04b-51fc-4710-b8fc-f68c34b89707	8c79766d-3b60-490b-a37c-b8e9798e6d4d
184b7192-7cbe-4f83-9b3a-6de96c101a4e	2023-08-29 14:13:01.017	2023-08-29 14:13:01.017	2a29e04b-51fc-4710-b8fc-f68c34b89707	84c127e6-5b1a-4914-877a-d95ac9070e90
51a745a9-da6b-4421-8f8a-4725b58b159d	2023-08-29 14:17:28.657	2023-08-29 14:17:28.657	2a29e04b-51fc-4710-b8fc-f68c34b89707	2b824e96-7146-4f51-a735-3bf2a1c0bf8c
47268a4e-0536-430c-9309-4748f417e6b3	2023-08-29 14:20:36.585	2023-08-29 14:20:36.585	2a29e04b-51fc-4710-b8fc-f68c34b89707	a270e152-2b7e-4af7-82be-cb51b489c4b4
fdd2656c-7782-4b61-9f4d-6c22a66531d8	2023-08-29 14:20:55.82	2023-08-29 14:20:55.82	2a29e04b-51fc-4710-b8fc-f68c34b89707	eda98b8c-bb21-4f90-8fe3-d7d122a1dfbc
786bb87e-5528-4c90-9205-6f0fc2b733ea	2023-08-29 14:21:19.673	2023-08-29 14:21:19.673	2a29e04b-51fc-4710-b8fc-f68c34b89707	d859b0b6-0de7-4652-bf4f-58d995cea871
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.users (id, active, city_of_birth, created_at, date_of_birth, email, fiscal_code, hash_password, name,
                   refresh_token, surname, updated_at) FROM stdin;
09933d03-d5af-40de-a915-89c24dabe84f	t	la spezia	2023-08-30 07:52:55.89	1996-03-16 00:00:00	balsamo@gmail.com	BLSCLL96D55E463O	$2a$10$WZmTfM2qfQmPz/IDO0q/6.rcKPCVKb9N.mIzH4WnLKAuGyrfeZ48S	balsamo		capelli	2023-08-30 07:52:55.89
320eb378-47ed-4316-87ce-1f344993e4dc	t	Sona	2023-08-29 13:55:43.562	1996-03-16 00:00:00	gaymommy@gmail.com	GYAMMY96C15I826C	$2a$10$BPlDEl7QjwkO5idQki.uWuxh3GgWpewR2/WZl6eIGOG.0o5Gx0e1m	gay	eyJhbGciOiJIUzM4NCJ9.eyJ0eXAiOiJKV1QiLCJpYXQiOjE2OTc3MjU3NzEsImV4cCI6MTcwMDMxNzc3MSwianRpIjoiYzFkMGNlMzQtZjlhOS00YjM0LWI5MDQtYjI1OGI3YzA1YTAzIiwic3ViIjoiMzIwZWIzNzgtNDdlZC00MzE2LTg3Y2UtMWYzNDQ5OTNlNGRjIn0.vRUZJhIJ5hJLIyThghH_A4qfc0HkYV31eBi0BEI2dZIFaN0n3h70fFeyZGpHgNwm	mommy	2023-08-29 13:55:43.562
\.


--
-- Data for Name: workers; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.workers (id, created_at, email, hash_password, refresh_token, updated_at, username, office_id) FROM stdin;
59bd23f4-512a-4e5b-96ad-977195aa8529	2023-08-29 14:02:55.284	email1	$2a$10$uNHYIUdtlrSobDprLw1rwe7Y7AC3kAQUq3v.UkMnCyqzqcPW6po6S	eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTMzMTc3NzUsImV4cCI6MTY5NTkwOTc3NSwianRpIjoiOTZkNWFiMTItZDM1My00ZDkyLWE4MDUtZTA2M2E1ZmU1NTYzIiwic3ViIjoiNTliZDIzZjQtNTEyYS00ZTViLTk2YWQtOTc3MTk1YWE4NTI5In0.7VV4Fs2mTnNbTsXEbXZKoTH7dhQfCxu97UFSd988lvk	2023-08-29 14:02:55.284	Samuel1	2a29e04b-51fc-4710-b8fc-f68c34b89707
3bcf1a7b-bc07-4218-bae3-68d375e8b819	2023-08-29 14:03:00.873	email2	$2a$10$8KD/F.sxm2Q3682IbSGHuOUcUmtRu0H9cemnF2BtFjK1YoDrSPkQy	eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTMzMTc3ODAsImV4cCI6MTY5NTkwOTc4MCwianRpIjoiY2Q5NzRkMDYtYTQ1Zi00YmIzLWI0YzMtMDZkZTFiNzBmOWI0Iiwic3ViIjoiM2JjZjFhN2ItYmMwNy00MjE4LWJhZTMtNjhkMzc1ZThiODE5In0.h5jyKt39rF7v4eYLE6Qyd8w--CTZ3raG4qictijEDIE	2023-08-29 14:03:00.873	Samuel2	2a29e04b-51fc-4710-b8fc-f68c34b89707
f7c0747f-d002-4c8e-b5dc-1973d21b24d5	2023-08-29 14:03:08.483	email3	$2a$10$UFCNYdPS.F61LTrjBW9mG.TVG/nq3sGRVLD6aWg9Nx51kI/TDEGz6	eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTMzMTc3ODgsImV4cCI6MTY5NTkwOTc4OCwianRpIjoiNWQ3NzRkZjEtZGU0NC00ODE2LWJjNDItZmIzOGRjN2NkZGJhIiwic3ViIjoiZjdjMDc0N2YtZDAwMi00YzhlLWI1ZGMtMTk3M2QyMWIyNGQ1In0.V3rVxS_4oUpZlnu74L6zavHSb0dJxq9vUP_zK6a9vIc	2023-08-29 14:03:08.483	Samuel3	2a29e04b-51fc-4710-b8fc-f68c34b89707
34a5bc6f-6ccb-4a91-b517-65bf5a53776c	2023-08-29 14:03:14.208	email4	$2a$10$VHEwZPrTZU5eeM47vjI2bOW6TAIoaDNDZXfkvYK/aoDEhjSNEJoOu	eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTMzMTc3OTQsImV4cCI6MTY5NTkwOTc5NCwianRpIjoiNTkwMTU4YzctMzMwMS00OGU3LTlmMGEtYzNlMWQ5YzZjZDNjIiwic3ViIjoiMzRhNWJjNmYtNmNjYi00YTkxLWI1MTctNjViZjVhNTM3NzZjIn0.A3LU5WKQ_oagdTprIQbmTojLlKRwqxuj3Dd_s5bH9Rk	2023-08-29 14:03:14.208	Samuel4	2a29e04b-51fc-4710-b8fc-f68c34b89707
eb1bb88e-4e32-4643-a124-b45fb25f0c12	2023-08-29 13:57:23.146	email5	$2a$10$I5M68Ogr0sZT1UrKz7hJUO0GjEbKCruLcMpgIquhKkqxxS4e70nq6	eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTMzMTg4MzMsImV4cCI6MTY5NTkxMDgzMywianRpIjoiYTBlZDkzODQtZTMwMS00ZDNlLThmZjEtMDIyNWU1ZDg0YjdmIiwic3ViIjoiZWIxYmI4OGUtNGUzMi00NjQzLWExMjQtYjQ1ZmIyNWYwYzEyIn0.lwzf835GwYo5tqDtyUzEGUE8ZE3a_MbF6q9crxMGT-Q	2023-08-29 13:57:23.146	LatinaMommy	0d7fe86a-d7a2-4581-adb2-b4bfd6fb10fd
\.


--
-- Name: availabilities availabilities_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.availabilities
    ADD CONSTRAINT availabilities_pkey PRIMARY KEY (id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: citizens_categories citizens_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.citizens_categories
    ADD CONSTRAINT citizens_categories_pkey PRIMARY KEY (id);


--
-- Name: citizens citizens_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.citizens
    ADD CONSTRAINT citizens_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: office_working_days office_working_days_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.office_working_days
    ADD CONSTRAINT office_working_days_pkey PRIMARY KEY (id);


--
-- Name: offices offices_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.offices
    ADD CONSTRAINT offices_pkey PRIMARY KEY (id);


--
-- Name: request_types request_types_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.request_types
    ADD CONSTRAINT request_types_pkey PRIMARY KEY (id);


--
-- Name: requests_offices requests_offices_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.requests_offices
    ADD CONSTRAINT requests_offices_pkey PRIMARY KEY (id);


--
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: workers uk_ad72yjctb2w3bn94m6dj5kxxk; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.workers
    ADD CONSTRAINT uk_ad72yjctb2w3bn94m6dj5kxxk UNIQUE (email);


--
-- Name: offices uk_edjms83xmpm0fdqiqya1a6qwt; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.offices
    ADD CONSTRAINT uk_edjms83xmpm0fdqiqya1a6qwt UNIQUE (name);


--
-- Name: citizens uk_fn0tfxc7lkrjbv4wldyswauwi; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.citizens
    ADD CONSTRAINT uk_fn0tfxc7lkrjbv4wldyswauwi UNIQUE (fiscal_code);


--
-- Name: workers uk_mhnorkgbqplbpclkg5uv3uvin; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.workers
    ADD CONSTRAINT uk_mhnorkgbqplbpclkg5uv3uvin UNIQUE (username);


--
-- Name: users uk_p8rpwek75uww1cmxdsofs8f78; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_p8rpwek75uww1cmxdsofs8f78 UNIQUE (fiscal_code);


--
-- Name: categories uk_t8o6pivur7nn124jehx7cygw5; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT uk_t8o6pivur7nn124jehx7cygw5 UNIQUE (name);


--
-- Name: request_types uk_ufco0qugr89lu386iw7rp4w2; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.request_types
    ADD CONSTRAINT uk_ufco0qugr89lu386iw7rp4w2 UNIQUE (name);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: workers workers_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.workers
    ADD CONSTRAINT workers_pkey PRIMARY KEY (id);


--
-- Name: citizens_categories fk14kq91118bvtyebi2xxmwqfd5; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.citizens_categories
    ADD CONSTRAINT fk14kq91118bvtyebi2xxmwqfd5 FOREIGN KEY (category_id) REFERENCES public.categories (id);


--
-- Name: citizens_categories fk1f4a2akk5idkk8q6h5jr2j86p; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.citizens_categories
    ADD CONSTRAINT fk1f4a2akk5idkk8q6h5jr2j86p FOREIGN KEY (citizen_id) REFERENCES public.citizens (id);


--
-- Name: availabilities fk6un9rntnvie082aoi6yea0wy1; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.availabilities
    ADD CONSTRAINT fk6un9rntnvie082aoi6yea0wy1 FOREIGN KEY (office_id) REFERENCES public.offices (id);


--
-- Name: requests_offices fk7jyk5944hhnfe4feu3040r98i; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.requests_offices
    ADD CONSTRAINT fk7jyk5944hhnfe4feu3040r98i FOREIGN KEY (office_id) REFERENCES public.offices (id);


--
-- Name: notifications fk8n5ej1w09r4c0i3up6w0k83kp; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk8n5ej1w09r4c0i3up6w0k83kp FOREIGN KEY (request_type_id) REFERENCES public.request_types (id);


--
-- Name: requests fk8oc543okq82yan71nvqj7hr79; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fk8oc543okq82yan71nvqj7hr79 FOREIGN KEY (worker_id) REFERENCES public.workers (id);


--
-- Name: workers fk9f87yafdk1c48quqxogd8vj74; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.workers
    ADD CONSTRAINT fk9f87yafdk1c48quqxogd8vj74 FOREIGN KEY (office_id) REFERENCES public.offices (id);


--
-- Name: notifications fk9y21adhxn0ayjhfocscqox7bh; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk9y21adhxn0ayjhfocscqox7bh FOREIGN KEY (user_id) REFERENCES public.users (id);


--
-- Name: availabilities fkb471teiejp5x0h6uvnoph1pke; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.availabilities
    ADD CONSTRAINT fkb471teiejp5x0h6uvnoph1pke FOREIGN KEY (request_id) REFERENCES public.requests (id);


--
-- Name: requests_offices fkgq9whtu7nrbrdtklo9j7403x4; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.requests_offices
    ADD CONSTRAINT fkgq9whtu7nrbrdtklo9j7403x4 FOREIGN KEY (request_id) REFERENCES public.requests (id);


--
-- Name: office_working_days fkhckna8ajomjqxxb78iytxcisc; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.office_working_days
    ADD CONSTRAINT fkhckna8ajomjqxxb78iytxcisc FOREIGN KEY (office_id) REFERENCES public.offices (id);


--
-- Name: notifications fkmb1e0bw1i3nyejmipyjw0numk; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fkmb1e0bw1i3nyejmipyjw0numk FOREIGN KEY (office_id) REFERENCES public.offices (id);


--
-- Name: availabilities fkr12nckjwkci4pi0cynhaifbkk; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.availabilities
    ADD CONSTRAINT fkr12nckjwkci4pi0cynhaifbkk FOREIGN KEY (user_id) REFERENCES public.users (id);


--
-- Name: requests fksnu8pyufweklimrfnyf6vrdcr; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fksnu8pyufweklimrfnyf6vrdcr FOREIGN KEY (request_type_id) REFERENCES public.request_types (id);


--
-- PostgreSQL database dump complete
--

