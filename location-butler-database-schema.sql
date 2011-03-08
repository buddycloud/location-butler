/*
 buddycloud location butler SQL schema
 File Encoding         : utf-8
*/

-- ----------------------------
--  Table structure for "spatial_ref_sys"
-- ----------------------------
DROP TABLE IF EXISTS "spatial_ref_sys";
CREATE TABLE "spatial_ref_sys" (
	"srid" int4 NOT NULL,
	"auth_name" varchar(256),
	"auth_srid" int4,
	"srtext" varchar(2048),
	"proj4text" varchar(2048)
)
WITH (OIDS=FALSE);
ALTER TABLE "spatial_ref_sys" OWNER TO "butler";

-- ----------------------------
--  Table structure for "geometry_columns"
-- ----------------------------
DROP TABLE IF EXISTS "geometry_columns";
CREATE TABLE "geometry_columns" (
	"f_table_catalog" varchar(256) NOT NULL,
	"f_table_schema" varchar(256) NOT NULL,
	"f_table_name" varchar(256) NOT NULL,
	"f_geometry_column" varchar(256) NOT NULL,
	"coord_dimension" int4 NOT NULL,
	"srid" int4 NOT NULL,
	"type" varchar(30) NOT NULL
)
WITH (OIDS=TRUE);
ALTER TABLE "geometry_columns" OWNER TO "butler";

-- ----------------------------
--  Table structure for "areacodes"
-- ----------------------------
DROP TABLE IF EXISTS "areacodes";
CREATE TABLE "areacodes" (
	"area_id" int4 NOT NULL DEFAULT nextval('butler.areacodes_area_id_seq'::regclass),
	"mcc" int4 NOT NULL,
	"mnc" int4 NOT NULL,
	"lac" int4 NOT NULL,
	"country" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"area" varchar(255) DEFAULT NULL::character varying
)
WITH (OIDS=FALSE);
ALTER TABLE "areacodes" OWNER TO "butler";

-- ----------------------------
--  Table structure for "place_subscriptions"
-- ----------------------------
DROP TABLE IF EXISTS "place_subscriptions";
CREATE TABLE "place_subscriptions" (
	"user_id" int4 NOT NULL,
	"place_id" int4 NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "place_subscriptions" OWNER TO "butler";

-- ----------------------------
--  Table structure for "beacon_info_3rdparty"
-- ----------------------------
DROP TABLE IF EXISTS "beacon_info_3rdparty";
CREATE TABLE "beacon_info_3rdparty" (
	"beacon_id" int4 NOT NULL,
	"lookup_time" date NOT NULL,
	"latitude" float8 NOT NULL,
	"longitude" float8 NOT NULL,
	"source" char(10) NOT NULL,
	"success" bool NOT NULL DEFAULT false,
	"range" float8
)
WITH (OIDS=FALSE);
ALTER TABLE "beacon_info_3rdparty" OWNER TO "butler";

-- ----------------------------
--  Table structure for "beacon_pattern_beacons"
-- ----------------------------
DROP TABLE IF EXISTS "beacon_pattern_beacons";
CREATE TABLE "beacon_pattern_beacons" (
	"pattern_id" int4 NOT NULL,
	"beacon_id" int4 NOT NULL,
	"time_fraction" float8 NOT NULL,
	"avg_signal_strength" float8 NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "beacon_pattern_beacons" OWNER TO "butler";

-- ----------------------------
--  Table structure for "beacon_patterns"
-- ----------------------------
DROP TABLE IF EXISTS "beacon_patterns";
CREATE TABLE "beacon_patterns" (
	"pattern_id" int4 NOT NULL DEFAULT nextval('butler.beacon_patterns_pattern_id_seq'::regclass),
	"place_id" int4 NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "beacon_patterns" OWNER TO "butler";

-- ----------------------------
--  Table structure for "comments"
-- ----------------------------
DROP TABLE IF EXISTS "comments";
CREATE TABLE "comments" (
	"id" int4 NOT NULL,
	"comment" text,
	"user_id" int4,
	"commentable_id" int4,
	"commentable_type" varchar(255) DEFAULT NULL::character varying,
	"created_at" timestamp(6) NULL,
	"updated_at" timestamp(6) NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "comments" OWNER TO "butler";

-- ----------------------------
--  Table structure for "help_messages_sent"
-- ----------------------------
DROP TABLE IF EXISTS "help_messages_sent";
CREATE TABLE "help_messages_sent" (
	"id" int4 NOT NULL DEFAULT nextval('butler.help_messages_sent_id_seq'::regclass),
	"timestamp" timestamp(6) NOT NULL DEFAULT now(),
	"user_id" int4 NOT NULL,
	"message_id" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "help_messages_sent" OWNER TO "butler";

-- ----------------------------
--  Table structure for "invites"
-- ----------------------------
DROP TABLE IF EXISTS "invites";
CREATE TABLE "invites" (
	"invite_id" int4 NOT NULL,
	"timestamp" timestamp(6) NOT NULL DEFAULT now(),
	"user_id" int4 NOT NULL,
	"phone_number" varchar(255) NOT NULL,
	"name" varchar(255) DEFAULT NULL::character varying
)
WITH (OIDS=FALSE);
ALTER TABLE "invites" OWNER TO "butler";

-- ----------------------------
--  Table structure for "location_history_backup"
-- ----------------------------
DROP TABLE IF EXISTS "location_history_backup";
CREATE TABLE "location_history_backup" (
	"history_id" int4 NOT NULL,
	"user_id" int4 NOT NULL,
	"timestamp" timestamp(6) NOT NULL,
	"label" varchar(255) NOT NULL,
	"latitude" float8,
	"longitude" float8,
	"place" varchar(255) DEFAULT NULL::character varying,
	"street" varchar(255) DEFAULT NULL::character varying,
	"area" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"postal_code" varchar(255) DEFAULT NULL::character varying,
	"country" varchar(255) DEFAULT NULL::character varying,
	"motion_state" varchar,
	"error" int4,
	"place_id" int4,
	"pattern_id" int4,
	"pattern_match" int4,
	"region" varchar(255) DEFAULT NULL::character varying
)
WITH (OIDS=FALSE);
ALTER TABLE "location_history_backup" OWNER TO "butler";

-- ----------------------------
--  Table structure for "long_strings"
-- ----------------------------
DROP TABLE IF EXISTS "long_strings";
CREATE TABLE "long_strings" (
	"id" int4 NOT NULL DEFAULT nextval('butler.long_strings_id_seq'::regclass),
	"lang" varchar(2) NOT NULL,
	"string_id" varchar(256) NOT NULL,
	"str" text
)
WITH (OIDS=FALSE);
ALTER TABLE "long_strings" OWNER TO "butler";

-- ----------------------------
--  Table structure for "place_overrides"
-- ----------------------------
DROP TABLE IF EXISTS "place_overrides";
CREATE TABLE "place_overrides" (
	"override_id" int4 NOT NULL DEFAULT nextval('butler.place_overrides_override_id_seq'::regclass),
	"user_id" int4 NOT NULL,
	"overridden_pattern_id" int4 NOT NULL,
	"overriding_pattern_id" int4 NOT NULL,
	"timestamp" timestamp(6) NOT NULL DEFAULT now()
)
WITH (OIDS=FALSE);
ALTER TABLE "place_overrides" OWNER TO "butler";

-- ----------------------------
--  Table structure for "standards_iso_3166"
-- ----------------------------
DROP TABLE IF EXISTS "standards_iso_3166";
CREATE TABLE "standards_iso_3166" (
	"iso" char(2) NOT NULL,
	"name" varchar(80) NOT NULL,
	"printable_name" varchar(80) NOT NULL,
	"iso3" char(3) DEFAULT NULL::bpchar,
	"numcode" int2
)
WITH (OIDS=FALSE);
ALTER TABLE "standards_iso_3166" OWNER TO "butler";

-- ----------------------------
--  Records of "standards_iso_3166"
-- ----------------------------
BEGIN;
INSERT INTO "standards_iso_3166" VALUES ('AF', 'AFGHANISTAN', 'Afghanistan', 'AFG', '4');
INSERT INTO "standards_iso_3166" VALUES ('AL', 'ALBANIA', 'Albania', 'ALB', '8');
INSERT INTO "standards_iso_3166" VALUES ('DZ', 'ALGERIA', 'Algeria', 'DZA', '12');
INSERT INTO "standards_iso_3166" VALUES ('AS', 'AMERICAN SAMOA', 'American Samoa', 'ASM', '16');
INSERT INTO "standards_iso_3166" VALUES ('AD', 'ANDORRA', 'Andorra', 'AND', '20');
INSERT INTO "standards_iso_3166" VALUES ('AO', 'ANGOLA', 'Angola', 'AGO', '24');
INSERT INTO "standards_iso_3166" VALUES ('AI', 'ANGUILLA', 'Anguilla', 'AIA', '660');
INSERT INTO "standards_iso_3166" VALUES ('AQ', 'ANTARCTICA', 'Antarctica', null, null);
INSERT INTO "standards_iso_3166" VALUES ('AG', 'ANTIGUA AND BARBUDA', 'Antigua and Barbuda', 'ATG', '28');
INSERT INTO "standards_iso_3166" VALUES ('AR', 'ARGENTINA', 'Argentina', 'ARG', '32');
INSERT INTO "standards_iso_3166" VALUES ('AM', 'ARMENIA', 'Armenia', 'ARM', '51');
INSERT INTO "standards_iso_3166" VALUES ('AW', 'ARUBA', 'Aruba', 'ABW', '533');
INSERT INTO "standards_iso_3166" VALUES ('AU', 'AUSTRALIA', 'Australia', 'AUS', '36');
INSERT INTO "standards_iso_3166" VALUES ('AT', 'AUSTRIA', 'Austria', 'AUT', '40');
INSERT INTO "standards_iso_3166" VALUES ('AZ', 'AZERBAIJAN', 'Azerbaijan', 'AZE', '31');
INSERT INTO "standards_iso_3166" VALUES ('BS', 'BAHAMAS', 'Bahamas', 'BHS', '44');
INSERT INTO "standards_iso_3166" VALUES ('BH', 'BAHRAIN', 'Bahrain', 'BHR', '48');
INSERT INTO "standards_iso_3166" VALUES ('BD', 'BANGLADESH', 'Bangladesh', 'BGD', '50');
INSERT INTO "standards_iso_3166" VALUES ('BB', 'BARBADOS', 'Barbados', 'BRB', '52');
INSERT INTO "standards_iso_3166" VALUES ('BY', 'BELARUS', 'Belarus', 'BLR', '112');
INSERT INTO "standards_iso_3166" VALUES ('BE', 'BELGIUM', 'Belgium', 'BEL', '56');
INSERT INTO "standards_iso_3166" VALUES ('BZ', 'BELIZE', 'Belize', 'BLZ', '84');
INSERT INTO "standards_iso_3166" VALUES ('BJ', 'BENIN', 'Benin', 'BEN', '204');
INSERT INTO "standards_iso_3166" VALUES ('BM', 'BERMUDA', 'Bermuda', 'BMU', '60');
INSERT INTO "standards_iso_3166" VALUES ('BT', 'BHUTAN', 'Bhutan', 'BTN', '64');
INSERT INTO "standards_iso_3166" VALUES ('BO', 'BOLIVIA', 'Bolivia', 'BOL', '68');
INSERT INTO "standards_iso_3166" VALUES ('BA', 'BOSNIA AND HERZEGOVINA', 'Bosnia and Herzegovina', 'BIH', '70');
INSERT INTO "standards_iso_3166" VALUES ('BW', 'BOTSWANA', 'Botswana', 'BWA', '72');
INSERT INTO "standards_iso_3166" VALUES ('BV', 'BOUVET ISLAND', 'Bouvet Island', null, null);
INSERT INTO "standards_iso_3166" VALUES ('BR', 'BRAZIL', 'Brazil', 'BRA', '76');
INSERT INTO "standards_iso_3166" VALUES ('IO', 'BRITISH INDIAN OCEAN TERRITORY', 'British Indian Ocean Territory', null, null);
INSERT INTO "standards_iso_3166" VALUES ('BN', 'BRUNEI DARUSSALAM', 'Brunei Darussalam', 'BRN', '96');
INSERT INTO "standards_iso_3166" VALUES ('BG', 'BULGARIA', 'Bulgaria', 'BGR', '100');
INSERT INTO "standards_iso_3166" VALUES ('BF', 'BURKINA FASO', 'Burkina Faso', 'BFA', '854');
INSERT INTO "standards_iso_3166" VALUES ('BI', 'BURUNDI', 'Burundi', 'BDI', '108');
INSERT INTO "standards_iso_3166" VALUES ('KH', 'CAMBODIA', 'Cambodia', 'KHM', '116');
INSERT INTO "standards_iso_3166" VALUES ('CM', 'CAMEROON', 'Cameroon', 'CMR', '120');
INSERT INTO "standards_iso_3166" VALUES ('CA', 'CANADA', 'Canada', 'CAN', '124');
INSERT INTO "standards_iso_3166" VALUES ('CV', 'CAPE VERDE', 'Cape Verde', 'CPV', '132');
INSERT INTO "standards_iso_3166" VALUES ('KY', 'CAYMAN ISLANDS', 'Cayman Islands', 'CYM', '136');
INSERT INTO "standards_iso_3166" VALUES ('CF', 'CENTRAL AFRICAN REPUBLIC', 'Central African Republic', 'CAF', '140');
INSERT INTO "standards_iso_3166" VALUES ('TD', 'CHAD', 'Chad', 'TCD', '148');
INSERT INTO "standards_iso_3166" VALUES ('CL', 'CHILE', 'Chile', 'CHL', '152');
INSERT INTO "standards_iso_3166" VALUES ('CN', 'CHINA', 'China', 'CHN', '156');
INSERT INTO "standards_iso_3166" VALUES ('CX', 'CHRISTMAS ISLAND', 'Christmas Island', null, null);
INSERT INTO "standards_iso_3166" VALUES ('CC', 'COCOS (KEELING) ISLANDS', 'Cocos (Keeling) Islands', null, null);
INSERT INTO "standards_iso_3166" VALUES ('CO', 'COLOMBIA', 'Colombia', 'COL', '170');
INSERT INTO "standards_iso_3166" VALUES ('KM', 'COMOROS', 'Comoros', 'COM', '174');
INSERT INTO "standards_iso_3166" VALUES ('CG', 'CONGO', 'Congo', 'COG', '178');
INSERT INTO "standards_iso_3166" VALUES ('CD', 'CONGO, THE DEMOCRATIC REPUBLIC OF THE', 'Congo, the Democratic Republic of the', 'COD', '180');
INSERT INTO "standards_iso_3166" VALUES ('CK', 'COOK ISLANDS', 'Cook Islands', 'COK', '184');
INSERT INTO "standards_iso_3166" VALUES ('CR', 'COSTA RICA', 'Costa Rica', 'CRI', '188');
INSERT INTO "standards_iso_3166" VALUES ('CI', 'COTE D''IVOIRE', 'Cote D''Ivoire', 'CIV', '384');
INSERT INTO "standards_iso_3166" VALUES ('HR', 'CROATIA', 'Croatia', 'HRV', '191');
INSERT INTO "standards_iso_3166" VALUES ('CU', 'CUBA', 'Cuba', 'CUB', '192');
INSERT INTO "standards_iso_3166" VALUES ('CY', 'CYPRUS', 'Cyprus', 'CYP', '196');
INSERT INTO "standards_iso_3166" VALUES ('CZ', 'CZECH REPUBLIC', 'Czech Republic', 'CZE', '203');
INSERT INTO "standards_iso_3166" VALUES ('DK', 'DENMARK', 'Denmark', 'DNK', '208');
INSERT INTO "standards_iso_3166" VALUES ('DJ', 'DJIBOUTI', 'Djibouti', 'DJI', '262');
INSERT INTO "standards_iso_3166" VALUES ('DM', 'DOMINICA', 'Dominica', 'DMA', '212');
INSERT INTO "standards_iso_3166" VALUES ('DO', 'DOMINICAN REPUBLIC', 'Dominican Republic', 'DOM', '214');
INSERT INTO "standards_iso_3166" VALUES ('EC', 'ECUADOR', 'Ecuador', 'ECU', '218');
INSERT INTO "standards_iso_3166" VALUES ('EG', 'EGYPT', 'Egypt', 'EGY', '818');
INSERT INTO "standards_iso_3166" VALUES ('SV', 'EL SALVADOR', 'El Salvador', 'SLV', '222');
INSERT INTO "standards_iso_3166" VALUES ('GQ', 'EQUATORIAL GUINEA', 'Equatorial Guinea', 'GNQ', '226');
INSERT INTO "standards_iso_3166" VALUES ('ER', 'ERITREA', 'Eritrea', 'ERI', '232');
INSERT INTO "standards_iso_3166" VALUES ('EE', 'ESTONIA', 'Estonia', 'EST', '233');
INSERT INTO "standards_iso_3166" VALUES ('ET', 'ETHIOPIA', 'Ethiopia', 'ETH', '231');
INSERT INTO "standards_iso_3166" VALUES ('FK', 'FALKLAND ISLANDS (MALVINAS)', 'Falkland Islands (Malvinas)', 'FLK', '238');
INSERT INTO "standards_iso_3166" VALUES ('FO', 'FAROE ISLANDS', 'Faroe Islands', 'FRO', '234');
INSERT INTO "standards_iso_3166" VALUES ('FJ', 'FIJI', 'Fiji', 'FJI', '242');
INSERT INTO "standards_iso_3166" VALUES ('FI', 'FINLAND', 'Finland', 'FIN', '246');
INSERT INTO "standards_iso_3166" VALUES ('FR', 'FRANCE', 'France', 'FRA', '250');
INSERT INTO "standards_iso_3166" VALUES ('GF', 'FRENCH GUIANA', 'French Guiana', 'GUF', '254');
INSERT INTO "standards_iso_3166" VALUES ('PF', 'FRENCH POLYNESIA', 'French Polynesia', 'PYF', '258');
INSERT INTO "standards_iso_3166" VALUES ('TF', 'FRENCH SOUTHERN TERRITORIES', 'French Southern Territories', null, null);
INSERT INTO "standards_iso_3166" VALUES ('GA', 'GABON', 'Gabon', 'GAB', '266');
INSERT INTO "standards_iso_3166" VALUES ('GM', 'GAMBIA', 'Gambia', 'GMB', '270');
INSERT INTO "standards_iso_3166" VALUES ('GE', 'GEORGIA', 'Georgia', 'GEO', '268');
INSERT INTO "standards_iso_3166" VALUES ('DE', 'GERMANY', 'Germany', 'DEU', '276');
INSERT INTO "standards_iso_3166" VALUES ('GH', 'GHANA', 'Ghana', 'GHA', '288');
INSERT INTO "standards_iso_3166" VALUES ('GI', 'GIBRALTAR', 'Gibraltar', 'GIB', '292');
INSERT INTO "standards_iso_3166" VALUES ('GR', 'GREECE', 'Greece', 'GRC', '300');
INSERT INTO "standards_iso_3166" VALUES ('GL', 'GREENLAND', 'Greenland', 'GRL', '304');
INSERT INTO "standards_iso_3166" VALUES ('GD', 'GRENADA', 'Grenada', 'GRD', '308');
INSERT INTO "standards_iso_3166" VALUES ('GP', 'GUADELOUPE', 'Guadeloupe', 'GLP', '312');
INSERT INTO "standards_iso_3166" VALUES ('GU', 'GUAM', 'Guam', 'GUM', '316');
INSERT INTO "standards_iso_3166" VALUES ('GT', 'GUATEMALA', 'Guatemala', 'GTM', '320');
INSERT INTO "standards_iso_3166" VALUES ('GN', 'GUINEA', 'Guinea', 'GIN', '324');
INSERT INTO "standards_iso_3166" VALUES ('GW', 'GUINEA-BISSAU', 'Guinea-Bissau', 'GNB', '624');
INSERT INTO "standards_iso_3166" VALUES ('GY', 'GUYANA', 'Guyana', 'GUY', '328');
INSERT INTO "standards_iso_3166" VALUES ('HT', 'HAITI', 'Haiti', 'HTI', '332');
INSERT INTO "standards_iso_3166" VALUES ('HM', 'HEARD ISLAND AND MCDONALD ISLANDS', 'Heard Island and Mcdonald Islands', null, null);
INSERT INTO "standards_iso_3166" VALUES ('VA', 'HOLY SEE (VATICAN CITY STATE)', 'Holy See (Vatican City State)', 'VAT', '336');
INSERT INTO "standards_iso_3166" VALUES ('HN', 'HONDURAS', 'Honduras', 'HND', '340');
INSERT INTO "standards_iso_3166" VALUES ('HK', 'HONG KONG', 'Hong Kong', 'HKG', '344');
INSERT INTO "standards_iso_3166" VALUES ('HU', 'HUNGARY', 'Hungary', 'HUN', '348');
INSERT INTO "standards_iso_3166" VALUES ('IS', 'ICELAND', 'Iceland', 'ISL', '352');
INSERT INTO "standards_iso_3166" VALUES ('IN', 'INDIA', 'India', 'IND', '356');
INSERT INTO "standards_iso_3166" VALUES ('ID', 'INDONESIA', 'Indonesia', 'IDN', '360');
INSERT INTO "standards_iso_3166" VALUES ('IR', 'IRAN, ISLAMIC REPUBLIC OF', 'Iran, Islamic Republic of', 'IRN', '364');
INSERT INTO "standards_iso_3166" VALUES ('IQ', 'IRAQ', 'Iraq', 'IRQ', '368');
INSERT INTO "standards_iso_3166" VALUES ('IE', 'IRELAND', 'Ireland', 'IRL', '372');
INSERT INTO "standards_iso_3166" VALUES ('IL', 'ISRAEL', 'Israel', 'ISR', '376');
INSERT INTO "standards_iso_3166" VALUES ('IT', 'ITALY', 'Italy', 'ITA', '380');
INSERT INTO "standards_iso_3166" VALUES ('JM', 'JAMAICA', 'Jamaica', 'JAM', '388');
INSERT INTO "standards_iso_3166" VALUES ('JP', 'JAPAN', 'Japan', 'JPN', '392');
INSERT INTO "standards_iso_3166" VALUES ('JO', 'JORDAN', 'Jordan', 'JOR', '400');
INSERT INTO "standards_iso_3166" VALUES ('KZ', 'KAZAKHSTAN', 'Kazakhstan', 'KAZ', '398');
INSERT INTO "standards_iso_3166" VALUES ('KE', 'KENYA', 'Kenya', 'KEN', '404');
INSERT INTO "standards_iso_3166" VALUES ('KI', 'KIRIBATI', 'Kiribati', 'KIR', '296');
INSERT INTO "standards_iso_3166" VALUES ('KP', 'KOREA, DEMOCRATIC PEOPLE''S REPUBLIC OF', 'Korea, Democratic People''s Republic of', 'PRK', '408');
INSERT INTO "standards_iso_3166" VALUES ('KR', 'KOREA, REPUBLIC OF', 'Korea, Republic of', 'KOR', '410');
INSERT INTO "standards_iso_3166" VALUES ('KW', 'KUWAIT', 'Kuwait', 'KWT', '414');
INSERT INTO "standards_iso_3166" VALUES ('KG', 'KYRGYZSTAN', 'Kyrgyzstan', 'KGZ', '417');
INSERT INTO "standards_iso_3166" VALUES ('LA', 'LAO PEOPLE''S DEMOCRATIC REPUBLIC', 'Lao People''s Democratic Republic', 'LAO', '418');
INSERT INTO "standards_iso_3166" VALUES ('LV', 'LATVIA', 'Latvia', 'LVA', '428');
INSERT INTO "standards_iso_3166" VALUES ('LB', 'LEBANON', 'Lebanon', 'LBN', '422');
INSERT INTO "standards_iso_3166" VALUES ('LS', 'LESOTHO', 'Lesotho', 'LSO', '426');
INSERT INTO "standards_iso_3166" VALUES ('LR', 'LIBERIA', 'Liberia', 'LBR', '430');
INSERT INTO "standards_iso_3166" VALUES ('LY', 'LIBYAN ARAB JAMAHIRIYA', 'Libyan Arab Jamahiriya', 'LBY', '434');
INSERT INTO "standards_iso_3166" VALUES ('LI', 'LIECHTENSTEIN', 'Liechtenstein', 'LIE', '438');
INSERT INTO "standards_iso_3166" VALUES ('LT', 'LITHUANIA', 'Lithuania', 'LTU', '440');
INSERT INTO "standards_iso_3166" VALUES ('LU', 'LUXEMBOURG', 'Luxembourg', 'LUX', '442');
INSERT INTO "standards_iso_3166" VALUES ('MO', 'MACAO', 'Macao', 'MAC', '446');
INSERT INTO "standards_iso_3166" VALUES ('MK', 'MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF', 'Macedonia, the Former Yugoslav Republic of', 'MKD', '807');
INSERT INTO "standards_iso_3166" VALUES ('MG', 'MADAGASCAR', 'Madagascar', 'MDG', '450');
INSERT INTO "standards_iso_3166" VALUES ('MW', 'MALAWI', 'Malawi', 'MWI', '454');
INSERT INTO "standards_iso_3166" VALUES ('MY', 'MALAYSIA', 'Malaysia', 'MYS', '458');
INSERT INTO "standards_iso_3166" VALUES ('MV', 'MALDIVES', 'Maldives', 'MDV', '462');
INSERT INTO "standards_iso_3166" VALUES ('ML', 'MALI', 'Mali', 'MLI', '466');
INSERT INTO "standards_iso_3166" VALUES ('MT', 'MALTA', 'Malta', 'MLT', '470');
INSERT INTO "standards_iso_3166" VALUES ('MH', 'MARSHALL ISLANDS', 'Marshall Islands', 'MHL', '584');
INSERT INTO "standards_iso_3166" VALUES ('MQ', 'MARTINIQUE', 'Martinique', 'MTQ', '474');
INSERT INTO "standards_iso_3166" VALUES ('MR', 'MAURITANIA', 'Mauritania', 'MRT', '478');
INSERT INTO "standards_iso_3166" VALUES ('MU', 'MAURITIUS', 'Mauritius', 'MUS', '480');
INSERT INTO "standards_iso_3166" VALUES ('YT', 'MAYOTTE', 'Mayotte', null, null);
INSERT INTO "standards_iso_3166" VALUES ('MX', 'MEXICO', 'Mexico', 'MEX', '484');
INSERT INTO "standards_iso_3166" VALUES ('FM', 'MICRONESIA, FEDERATED STATES OF', 'Micronesia, Federated States of', 'FSM', '583');
INSERT INTO "standards_iso_3166" VALUES ('MD', 'MOLDOVA, REPUBLIC OF', 'Moldova, Republic of', 'MDA', '498');
INSERT INTO "standards_iso_3166" VALUES ('MC', 'MONACO', 'Monaco', 'MCO', '492');
INSERT INTO "standards_iso_3166" VALUES ('MN', 'MONGOLIA', 'Mongolia', 'MNG', '496');
INSERT INTO "standards_iso_3166" VALUES ('MS', 'MONTSERRAT', 'Montserrat', 'MSR', '500');
INSERT INTO "standards_iso_3166" VALUES ('MA', 'MOROCCO', 'Morocco', 'MAR', '504');
INSERT INTO "standards_iso_3166" VALUES ('MZ', 'MOZAMBIQUE', 'Mozambique', 'MOZ', '508');
INSERT INTO "standards_iso_3166" VALUES ('MM', 'MYANMAR', 'Myanmar', 'MMR', '104');
INSERT INTO "standards_iso_3166" VALUES ('NA', 'NAMIBIA', 'Namibia', 'NAM', '516');
INSERT INTO "standards_iso_3166" VALUES ('NR', 'NAURU', 'Nauru', 'NRU', '520');
INSERT INTO "standards_iso_3166" VALUES ('NP', 'NEPAL', 'Nepal', 'NPL', '524');
INSERT INTO "standards_iso_3166" VALUES ('NL', 'NETHERLANDS', 'Netherlands', 'NLD', '528');
INSERT INTO "standards_iso_3166" VALUES ('AN', 'NETHERLANDS ANTILLES', 'Netherlands Antilles', 'ANT', '530');
INSERT INTO "standards_iso_3166" VALUES ('NC', 'NEW CALEDONIA', 'New Caledonia', 'NCL', '540');
INSERT INTO "standards_iso_3166" VALUES ('NZ', 'NEW ZEALAND', 'New Zealand', 'NZL', '554');
INSERT INTO "standards_iso_3166" VALUES ('NI', 'NICARAGUA', 'Nicaragua', 'NIC', '558');
INSERT INTO "standards_iso_3166" VALUES ('NE', 'NIGER', 'Niger', 'NER', '562');
INSERT INTO "standards_iso_3166" VALUES ('NG', 'NIGERIA', 'Nigeria', 'NGA', '566');
INSERT INTO "standards_iso_3166" VALUES ('NU', 'NIUE', 'Niue', 'NIU', '570');
INSERT INTO "standards_iso_3166" VALUES ('NF', 'NORFOLK ISLAND', 'Norfolk Island', 'NFK', '574');
INSERT INTO "standards_iso_3166" VALUES ('MP', 'NORTHERN MARIANA ISLANDS', 'Northern Mariana Islands', 'MNP', '580');
INSERT INTO "standards_iso_3166" VALUES ('NO', 'NORWAY', 'Norway', 'NOR', '578');
INSERT INTO "standards_iso_3166" VALUES ('OM', 'OMAN', 'Oman', 'OMN', '512');
INSERT INTO "standards_iso_3166" VALUES ('PK', 'PAKISTAN', 'Pakistan', 'PAK', '586');
INSERT INTO "standards_iso_3166" VALUES ('PW', 'PALAU', 'Palau', 'PLW', '585');
INSERT INTO "standards_iso_3166" VALUES ('PS', 'PALESTINIAN TERRITORY, OCCUPIED', 'Palestinian Territory, Occupied', null, null);
INSERT INTO "standards_iso_3166" VALUES ('PA', 'PANAMA', 'Panama', 'PAN', '591');
INSERT INTO "standards_iso_3166" VALUES ('PG', 'PAPUA NEW GUINEA', 'Papua New Guinea', 'PNG', '598');
INSERT INTO "standards_iso_3166" VALUES ('PY', 'PARAGUAY', 'Paraguay', 'PRY', '600');
INSERT INTO "standards_iso_3166" VALUES ('PE', 'PERU', 'Peru', 'PER', '604');
INSERT INTO "standards_iso_3166" VALUES ('PH', 'PHILIPPINES', 'Philippines', 'PHL', '608');
INSERT INTO "standards_iso_3166" VALUES ('PN', 'PITCAIRN', 'Pitcairn', 'PCN', '612');
INSERT INTO "standards_iso_3166" VALUES ('PL', 'POLAND', 'Poland', 'POL', '616');
INSERT INTO "standards_iso_3166" VALUES ('PT', 'PORTUGAL', 'Portugal', 'PRT', '620');
INSERT INTO "standards_iso_3166" VALUES ('PR', 'PUERTO RICO', 'Puerto Rico', 'PRI', '630');
INSERT INTO "standards_iso_3166" VALUES ('QA', 'QATAR', 'Qatar', 'QAT', '634');
INSERT INTO "standards_iso_3166" VALUES ('RE', 'REUNION', 'Reunion', 'REU', '638');
INSERT INTO "standards_iso_3166" VALUES ('RO', 'ROMANIA', 'Romania', 'ROM', '642');
INSERT INTO "standards_iso_3166" VALUES ('RU', 'RUSSIAN FEDERATION', 'Russian Federation', 'RUS', '643');
INSERT INTO "standards_iso_3166" VALUES ('RW', 'RWANDA', 'Rwanda', 'RWA', '646');
INSERT INTO "standards_iso_3166" VALUES ('SH', 'SAINT HELENA', 'Saint Helena', 'SHN', '654');
INSERT INTO "standards_iso_3166" VALUES ('KN', 'SAINT KITTS AND NEVIS', 'Saint Kitts and Nevis', 'KNA', '659');
INSERT INTO "standards_iso_3166" VALUES ('LC', 'SAINT LUCIA', 'Saint Lucia', 'LCA', '662');
INSERT INTO "standards_iso_3166" VALUES ('PM', 'SAINT PIERRE AND MIQUELON', 'Saint Pierre and Miquelon', 'SPM', '666');
INSERT INTO "standards_iso_3166" VALUES ('VC', 'SAINT VINCENT AND THE GRENADINES', 'Saint Vincent and the Grenadines', 'VCT', '670');
INSERT INTO "standards_iso_3166" VALUES ('WS', 'SAMOA', 'Samoa', 'WSM', '882');
INSERT INTO "standards_iso_3166" VALUES ('SM', 'SAN MARINO', 'San Marino', 'SMR', '674');
INSERT INTO "standards_iso_3166" VALUES ('ST', 'SAO TOME AND PRINCIPE', 'Sao Tome and Principe', 'STP', '678');
INSERT INTO "standards_iso_3166" VALUES ('SA', 'SAUDI ARABIA', 'Saudi Arabia', 'SAU', '682');
INSERT INTO "standards_iso_3166" VALUES ('SN', 'SENEGAL', 'Senegal', 'SEN', '686');
INSERT INTO "standards_iso_3166" VALUES ('CS', 'SERBIA AND MONTENEGRO', 'Serbia and Montenegro', null, null);
INSERT INTO "standards_iso_3166" VALUES ('SC', 'SEYCHELLES', 'Seychelles', 'SYC', '690');
INSERT INTO "standards_iso_3166" VALUES ('SL', 'SIERRA LEONE', 'Sierra Leone', 'SLE', '694');
INSERT INTO "standards_iso_3166" VALUES ('SG', 'SINGAPORE', 'Singapore', 'SGP', '702');
INSERT INTO "standards_iso_3166" VALUES ('SK', 'SLOVAKIA', 'Slovakia', 'SVK', '703');
INSERT INTO "standards_iso_3166" VALUES ('SI', 'SLOVENIA', 'Slovenia', 'SVN', '705');
INSERT INTO "standards_iso_3166" VALUES ('SB', 'SOLOMON ISLANDS', 'Solomon Islands', 'SLB', '90');
INSERT INTO "standards_iso_3166" VALUES ('SO', 'SOMALIA', 'Somalia', 'SOM', '706');
INSERT INTO "standards_iso_3166" VALUES ('ZA', 'SOUTH AFRICA', 'South Africa', 'ZAF', '710');
INSERT INTO "standards_iso_3166" VALUES ('GS', 'SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS', 'South Georgia and the South Sandwich Islands', null, null);
INSERT INTO "standards_iso_3166" VALUES ('ES', 'SPAIN', 'Spain', 'ESP', '724');
INSERT INTO "standards_iso_3166" VALUES ('LK', 'SRI LANKA', 'Sri Lanka', 'LKA', '144');
INSERT INTO "standards_iso_3166" VALUES ('SD', 'SUDAN', 'Sudan', 'SDN', '736');
INSERT INTO "standards_iso_3166" VALUES ('SR', 'SURINAME', 'Suriname', 'SUR', '740');
INSERT INTO "standards_iso_3166" VALUES ('SJ', 'SVALBARD AND JAN MAYEN', 'Svalbard and Jan Mayen', 'SJM', '744');
INSERT INTO "standards_iso_3166" VALUES ('SZ', 'SWAZILAND', 'Swaziland', 'SWZ', '748');
INSERT INTO "standards_iso_3166" VALUES ('SE', 'SWEDEN', 'Sweden', 'SWE', '752');
INSERT INTO "standards_iso_3166" VALUES ('CH', 'SWITZERLAND', 'Switzerland', 'CHE', '756');
INSERT INTO "standards_iso_3166" VALUES ('SY', 'SYRIAN ARAB REPUBLIC', 'Syrian Arab Republic', 'SYR', '760');
INSERT INTO "standards_iso_3166" VALUES ('TW', 'TAIWAN, PROVINCE OF CHINA', 'Taiwan, Province of China', 'TWN', '158');
INSERT INTO "standards_iso_3166" VALUES ('TJ', 'TAJIKISTAN', 'Tajikistan', 'TJK', '762');
INSERT INTO "standards_iso_3166" VALUES ('TZ', 'TANZANIA, UNITED REPUBLIC OF', 'Tanzania, United Republic of', 'TZA', '834');
INSERT INTO "standards_iso_3166" VALUES ('TH', 'THAILAND', 'Thailand', 'THA', '764');
INSERT INTO "standards_iso_3166" VALUES ('TL', 'TIMOR-LESTE', 'Timor-Leste', null, null);
INSERT INTO "standards_iso_3166" VALUES ('TG', 'TOGO', 'Togo', 'TGO', '768');
INSERT INTO "standards_iso_3166" VALUES ('TK', 'TOKELAU', 'Tokelau', 'TKL', '772');
INSERT INTO "standards_iso_3166" VALUES ('TO', 'TONGA', 'Tonga', 'TON', '776');
INSERT INTO "standards_iso_3166" VALUES ('TT', 'TRINIDAD AND TOBAGO', 'Trinidad and Tobago', 'TTO', '780');
INSERT INTO "standards_iso_3166" VALUES ('TN', 'TUNISIA', 'Tunisia', 'TUN', '788');
INSERT INTO "standards_iso_3166" VALUES ('TR', 'TURKEY', 'Turkey', 'TUR', '792');
INSERT INTO "standards_iso_3166" VALUES ('TM', 'TURKMENISTAN', 'Turkmenistan', 'TKM', '795');
INSERT INTO "standards_iso_3166" VALUES ('TC', 'TURKS AND CAICOS ISLANDS', 'Turks and Caicos Islands', 'TCA', '796');
INSERT INTO "standards_iso_3166" VALUES ('TV', 'TUVALU', 'Tuvalu', 'TUV', '798');
INSERT INTO "standards_iso_3166" VALUES ('UG', 'UGANDA', 'Uganda', 'UGA', '800');
INSERT INTO "standards_iso_3166" VALUES ('UA', 'UKRAINE', 'Ukraine', 'UKR', '804');
INSERT INTO "standards_iso_3166" VALUES ('AE', 'UNITED ARAB EMIRATES', 'United Arab Emirates', 'ARE', '784');
INSERT INTO "standards_iso_3166" VALUES ('GB', 'UNITED KINGDOM', 'United Kingdom', 'GBR', '826');
INSERT INTO "standards_iso_3166" VALUES ('US', 'UNITED STATES', 'United States', 'USA', '840');
INSERT INTO "standards_iso_3166" VALUES ('UM', 'UNITED STATES MINOR OUTLYING ISLANDS', 'United States Minor Outlying Islands', null, null);
INSERT INTO "standards_iso_3166" VALUES ('UY', 'URUGUAY', 'Uruguay', 'URY', '858');
INSERT INTO "standards_iso_3166" VALUES ('UZ', 'UZBEKISTAN', 'Uzbekistan', 'UZB', '860');
INSERT INTO "standards_iso_3166" VALUES ('VU', 'VANUATU', 'Vanuatu', 'VUT', '548');
INSERT INTO "standards_iso_3166" VALUES ('VE', 'VENEZUELA', 'Venezuela', 'VEN', '862');
INSERT INTO "standards_iso_3166" VALUES ('VN', 'VIET NAM', 'Viet Nam', 'VNM', '704');
INSERT INTO "standards_iso_3166" VALUES ('VG', 'VIRGIN ISLANDS, BRITISH', 'Virgin Islands, British', 'VGB', '92');
INSERT INTO "standards_iso_3166" VALUES ('VI', 'VIRGIN ISLANDS, U.S.', 'Virgin Islands, U.s.', 'VIR', '850');
INSERT INTO "standards_iso_3166" VALUES ('WF', 'WALLIS AND FUTUNA', 'Wallis and Futuna', 'WLF', '876');
INSERT INTO "standards_iso_3166" VALUES ('EH', 'WESTERN SAHARA', 'Western Sahara', 'ESH', '732');
INSERT INTO "standards_iso_3166" VALUES ('YE', 'YEMEN', 'Yemen', 'YEM', '887');
INSERT INTO "standards_iso_3166" VALUES ('ZM', 'ZAMBIA', 'Zambia', 'ZMB', '894');
INSERT INTO "standards_iso_3166" VALUES ('ZW', 'ZIMBABWE', 'Zimbabwe', 'ZWE', '716');
COMMIT;

-- ----------------------------
--  Table structure for "beacon_observations"
-- ----------------------------
DROP TABLE IF EXISTS "beacon_observations";
CREATE TABLE "beacon_observations" (
	"oid" int4 NOT NULL DEFAULT nextval('butler.beacon_observations_oid_seq'::regclass),
	"beacon_id" int4 NOT NULL,
	"latitude" float8 NOT NULL,
	"longitude" float8 NOT NULL,
	"time" timestamp(6) NOT NULL DEFAULT now(),
	"accuracy" float8
)
WITH (OIDS=FALSE);
ALTER TABLE "beacon_observations" OWNER TO "butler";

-- ----------------------------
--  Table structure for "places"
-- ----------------------------
DROP TABLE IF EXISTS "places";
CREATE TABLE "places" (
	"place_id" int4 NOT NULL DEFAULT nextval('butler.places_place_id_seq'::regclass),
	"defined_by" int4 NOT NULL,
	"defined_time" timestamp(6) WITH TIME ZONE NOT NULL DEFAULT now(),
	"name" varchar(255) NOT NULL DEFAULT NULL::character varying,
	"description" varchar(255) DEFAULT NULL::character varying,
	"site_url" varchar(255) DEFAULT NULL::character varying,
	"wiki_url" varchar(255) DEFAULT NULL::character varying,
	"is_public" bool NOT NULL DEFAULT true,
	"street" varchar(255) DEFAULT NULL::character varying,
	"area" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"postal_code" varchar(255) DEFAULT NULL::character varying,
	"postal_code_after_city" int2,
	"district" varchar(255) DEFAULT NULL::character varying,
	"country" varchar(255) NOT NULL DEFAULT NULL::character varying,
	"country_code" varchar(255) DEFAULT NULL::character varying,
	"latitude" float8,
	"longitude" float8,
	"range" varchar(255) DEFAULT NULL::character varying,
	"pos_source" varchar,
	"geom" "geometry",
	"region" varchar(255) DEFAULT NULL::character varying,
	"accuracy" float8,
	"revision" int4
)
WITH (OIDS=FALSE);
ALTER TABLE "places" OWNER TO "butler";

-- ----------------------------
--  Table structure for "country"
-- ----------------------------
DROP TABLE IF EXISTS "country";
CREATE TABLE "country" (
	"code" char(2) NOT NULL,
	"name" varchar(100) NOT NULL,
	"continent" char(2) NOT NULL,
	"capital" varchar(100) NOT NULL,
	"lat" float8 NOT NULL,
	"lon" float8 NOT NULL,
	"area" int4 NOT NULL,
	"population" int4 NOT NULL,
	"tld" char(3) NOT NULL,
	"currcode" char(3) NOT NULL,
	"currname" varchar(20) NOT NULL,
	"phone" varchar(20) NOT NULL,
	"postformat" varchar(20) NOT NULL,
	"postregex" varchar(60) NOT NULL,
	"neighbors" varchar(60) NOT NULL,
	"geoid" int4 NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "country" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "continent"
-- ----------------------------
DROP TABLE IF EXISTS "continent";
CREATE TABLE "continent" (
	"code" char(2) NOT NULL,
	"name" varchar(20) NOT NULL,
	"lat" float8 NOT NULL,
	"lon" float8 NOT NULL,
	"geoid" int4 NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "continent" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "query_beacons"
-- ----------------------------
DROP TABLE IF EXISTS "query_beacons";
CREATE TABLE "query_beacons" (
	"log_id" int4 NOT NULL DEFAULT 0,
	"beacon_id" int4 NOT NULL DEFAULT 0,
	"signal_strength" int4 DEFAULT 0
)
WITH (OIDS=FALSE);
ALTER TABLE "query_beacons" OWNER TO "butler";

-- ----------------------------
--  Table structure for "location_users"
-- ----------------------------
DROP TABLE IF EXISTS "location_users";
CREATE TABLE "location_users" (
	"user_id" int4 NOT NULL DEFAULT nextval('ejabberd.user_id_seq'::regclass),
	"jid" varchar(150) NOT NULL,
	"created" timestamp(6) NOT NULL DEFAULT now()
)
WITH (OIDS=FALSE);
ALTER TABLE "location_users" OWNER TO "butler";

-- ----------------------------
--  Table structure for "location_history"
-- ----------------------------
DROP TABLE IF EXISTS "location_history";
CREATE TABLE "location_history" (
	"history_id" int4 NOT NULL DEFAULT nextval('butler.location_history_history_id_seq'::regclass),
	"user_id" int4 NOT NULL,
	"timestamp" timestamp(6) NOT NULL DEFAULT now(),
	"label" varchar(255) NOT NULL,
	"latitude" float8,
	"longitude" float8,
	"place" varchar(255) DEFAULT NULL::character varying,
	"street" varchar(255) DEFAULT NULL::character varying,
	"area" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"postal_code" varchar(255) DEFAULT NULL::character varying,
	"country" varchar(255) DEFAULT NULL::character varying,
	"motion_state" varchar,
	"error" int4,
	"place_id" int4,
	"pattern_id" int4,
	"pattern_match" int4,
	"region" varchar(255) DEFAULT NULL::character varying
)
WITH (OIDS=FALSE);
ALTER TABLE "location_history" OWNER TO "butler";

-- ----------------------------
--  Table structure for "beacons"
-- ----------------------------
DROP TABLE IF EXISTS "beacons";
CREATE TABLE "beacons" (
	"beacon_id" int4 NOT NULL DEFAULT nextval('butler.beacons_beacon_id_seq'::regclass),
	"beacon_type" varchar(255) NOT NULL,
	"mac" varchar(64) NOT NULL,
	"encoding" varchar,
	"country" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"area" varchar(255) DEFAULT NULL::character varying,
	"latitude" float8,
	"longitude" float8,
	"is_fixed" bool NOT NULL DEFAULT false,
	"pos_source" varchar,
	"range" int4,
	"region" varchar(255) DEFAULT NULL::character varying
)
WITH (OIDS=FALSE);
ALTER TABLE "beacons" OWNER TO "butler";

-- ----------------------------
--  Table structure for "current_locations"
-- ----------------------------
DROP TABLE IF EXISTS "current_locations";
CREATE TABLE "current_locations" (
	"user_id" int4 NOT NULL,
	"timestamp" timestamp(6) NOT NULL DEFAULT now(),
	"label" varchar(255) NOT NULL,
	"latitude" float8,
	"longitude" float8,
	"place" varchar(255) DEFAULT NULL::character varying,
	"street" varchar(255) DEFAULT NULL::character varying,
	"area" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"postal_code" varchar(255) DEFAULT NULL::character varying,
	"country" varchar(255) DEFAULT NULL::character varying,
	"motion_state" varchar,
	"error" int4,
	"place_id" int4,
	"pattern_id" int4,
	"pattern_match" int4,
	"region" varchar(255) DEFAULT NULL::character varying,
	"geom" "geometry"
)
WITH (OIDS=FALSE);
ALTER TABLE "current_locations" OWNER TO "butler";

-- ----------------------------
--  Table structure for "next_locations"
-- ----------------------------
DROP TABLE IF EXISTS "next_locations";
CREATE TABLE "next_locations" (
	"next_location_id" int4 NOT NULL DEFAULT nextval('butler.next_locations_next_location_id_seq'::regclass),
	"user_id" int4 NOT NULL,
	"label" varchar(255) NOT NULL,
	"place_id" int4 NOT NULL DEFAULT 0,
	"timestamp" timestamp(6) NOT NULL DEFAULT now()
)
WITH (OIDS=FALSE);
ALTER TABLE "next_locations" OWNER TO "butler";

-- ----------------------------
--  Table structure for "place_history"
-- ----------------------------
DROP TABLE IF EXISTS "place_history";
CREATE TABLE "place_history" (
	"user_id" int4 NOT NULL,
	"place_id" int4 NOT NULL,
	"entry_time" timestamp(6) NOT NULL DEFAULT now(),
	"exit_time" timestamp(6) NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "place_history" OWNER TO "butler";

-- ----------------------------
--  Table structure for "queries"
-- ----------------------------
DROP TABLE IF EXISTS "queries";
CREATE TABLE "queries" (
	"log_id" int4 NOT NULL DEFAULT nextval('butler.queries_log_id_seq'::regclass),
	"user_id" int4 NOT NULL,
	"client_time" timestamp(6) NULL,
	"latitude" float8,
	"longitude" float8,
	"error" int4,
	"timestamp" timestamp(6) NOT NULL DEFAULT now()
)
WITH (OIDS=FALSE);
ALTER TABLE "queries" OWNER TO "butler";

-- ----------------------------
--  Table structure for "standards_mcc"
-- ----------------------------
DROP TABLE IF EXISTS "standards_mcc";
CREATE TABLE "standards_mcc" (
	"mcc" int4 NOT NULL,
	"country_name" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "standards_mcc" OWNER TO "butler";

-- ----------------------------
--  Function structure for bc_get_channel_location(varchar)
-- ----------------------------
DROP FUNCTION IF EXISTS "bc_get_channel_location"(varchar);
CREATE FUNCTION "bc_get_channel_location"(IN room_name varchar, OUT lat float8, OUT lon float8) RETURNS "record" 
	AS $BODY$
DECLARE
	location		Geometry;
BEGIN
	SELECT cl.geom INTO location
	FROM palaver.Muc_Rooms r, palaver.Muc_Rooms_Owners o, palaver.Muc_Users u,
		Location_Users lu, Current_Locations cl
	WHERE r.name = room_name AND r.id = o.room_id AND o.user_id = u.id AND
		u.username = lu.jid AND lu.user_id = cl.user_id;
	-- (this is a somewhat expensive query as you will notice, but it's the only
	--  way to go if we don't sync user ids.)	
	lat := y(location); -- PostGIS points are (long, lat)!
	lon := x(location);
END
$BODY$
	LANGUAGE plpgsql
	COST 100
	CALLED ON NULL INPUT
	SECURITY INVOKER
	VOLATILE;
ALTER FUNCTION "bc_get_channel_location"(IN room_name varchar, OUT lat float8, OUT lon float8) OWNER TO "postgres";

-- ----------------------------
--  Function structure for bc_nearest_channel(float8, float8, timestamp, int4)
-- ----------------------------
DROP FUNCTION IF EXISTS "bc_nearest_channel"(float8, float8, timestamp, int4);
CREATE FUNCTION "bc_nearest_channel"(IN lat float8, IN lon float8, IN since timestamp, IN "k" int4) RETURNS SETOF "public"."bc_nearest_channel_type" 
	AS $BODY$DECLARE
	location 	Geometry;
BEGIN
	-- create the internal representation of the given lat/lon coords
	location := GeometryFromText('POINT(' || lon || ' ' || lat || ')', 4326);
	-- retrieve channels close by
	RETURN QUERY
		SELECT r.name, r.roomname, r.hostname, r.subject, 
			substring(r.description from 1 for 2048)::Varchar AS description, u.username,
			cl.country, cl.region, cl.city, cl.area, cl.latitude, cl.longitude, 
			ST_Distance_Sphere(location, geom) AS distance, 
			r.popularity, r.relpop, r.rank
		FROM palaver.Muc_Rooms r, palaver.Muc_Rooms_Owners o, palaver.Muc_Users u,
			Location_Users lu, Current_Locations cl
		WHERE r.id = o.room_id AND o.user_id = u.id AND 
			u.username = lu.jid AND lu.user_id = cl.user_id AND
			NOT (cl.geom IS NULL OR cl.geom = GeometryFromText('POINT(0 0)', 4326))
			AND cl."timestamp" >= since AND personal = false AND hidden != true
		ORDER BY ST_Distance_Sphere(location, cl.geom)
		LIMIT k;
END
$BODY$
	LANGUAGE plpgsql
	COST 100
	ROWS 1000
	CALLED ON NULL INPUT
	SECURITY INVOKER
	VOLATILE;
ALTER FUNCTION "bc_nearest_channel"(IN lat float8, IN lon float8, IN since timestamp, IN "k" int4) OWNER TO "postgres";
COMMENT ON FUNCTION "bc_nearest_channel"(IN lat float8, IN lon float8, IN since timestamp, IN "k" int4) IS 'Returns the k channels closest to the given (lat, long) coordinates';

-- ----------------------------
--  Function structure for bc_nearest_channel(varchar, timestamp, int4)
-- ----------------------------
DROP FUNCTION IF EXISTS "bc_nearest_channel"(varchar, timestamp, int4);
CREATE FUNCTION "bc_nearest_channel"(IN jjid varchar, IN since timestamp, IN "k" int4) RETURNS SETOF "public"."bc_nearest_channel_type" 
	AS $BODY$DECLARE
	location 	Geometry;
BEGIN
	-- (1) determine the channel's owner's current location, if there is such
	SELECT geom INTO location 
	FROM Current_Locations l, Location_Users u
	WHERE u.jid = jjid AND l.user_id = u.user_id AND l."timestamp" >= since;
	IF NOT FOUND THEN
        RAISE EXCEPTION 'There is no user with jid % in Current_Locations', jjid;
	ELSIF location IS NULL THEN
		RAISE EXCEPTION 'User % does not have a current location since %, so cannot determine nearest neighbors', jjid, since;
	END IF;
	-- (2) return all the nearest channels that the given user does not own
	RETURN QUERY
		SELECT r.name, r.roomname, r.hostname, r.subject, 
			substring(r.description from 1 for 2048)::Varchar AS description, u.username,
			cl.country, cl.region, cl.city, cl.area, cl.latitude, cl.longitude, 
			ST_Distance_Sphere(location, geom) AS distance, 
			r.popularity, r.relpop, r.rank
		FROM palaver.Muc_Rooms r, palaver.Muc_Rooms_Owners o, palaver.Muc_Users u,
			Location_Users lu, Current_Locations cl
		WHERE r.id = o.room_id AND o.user_id = u.id AND u.username = lu.jid 
			AND u.username != jjid AND lu.user_id = cl.user_id AND
			NOT (cl.geom IS NULL OR cl.geom = GeometryFromText('POINT(0 0)', 4326))
			AND cl."timestamp" >= since AND personal = false AND hidden != true
		ORDER BY ST_Distance_Sphere(location, cl.geom)
		LIMIT k;
	RETURN;
END
$BODY$
	LANGUAGE plpgsql
	COST 100
	ROWS 1000
	CALLED ON NULL INPUT
	SECURITY INVOKER
	VOLATILE;
ALTER FUNCTION "bc_nearest_channel"(IN jjid varchar, IN since timestamp, IN "k" int4) OWNER TO "postgres";
COMMENT ON FUNCTION "bc_nearest_channel"(IN jjid varchar, IN since timestamp, IN "k" int4) IS 'Returns the k channels closest to the user with with the given jid not including any of this user''s channels';

-- ----------------------------
--  Function structure for bc_nearest_neighbor(varchar, timestamp, int4)
-- ----------------------------
DROP FUNCTION IF EXISTS "bc_nearest_neighbor"(varchar, timestamp, int4);
CREATE FUNCTION "bc_nearest_neighbor"(IN uname varchar, IN since timestamp, IN "k" int4) RETURNS SETOF "public"."bc_nearest_neighbor_type" 
	AS $BODY$
DECLARE
    location 	Geometry;
BEGIN
	-- Determine the PostGIS representation of the given user's current location
	SELECT geom INTO location 
	FROM Current_Locations
	WHERE username = uname;
	IF NOT FOUND THEN
        RAISE EXCEPTION 'There is no user with name % in Current_Locations', uname;
	ELSIF location IS NULL THEN
		RAISE EXCEPTION 'User % does not have a current location since %, so cannot determine nearest neighbors', uid, since;
	END IF;
	RETURN QUERY
		SELECT CL.user_id, CL.place_id, CL.place, CL.country, CL.region, 
			CL.city, CL.area, ST_Distance_Sphere(loc, CL.geom)
		FROM Current_Locations CL
		WHERE CL.geom IS NOT NULL AND CL."timestamp" >= since 
		      AND CL.user_id <> uuid
		ORDER BY ST_Distance_Sphere(loc, CL.geom)
		LIMIT k;
	RETURN;
END
$BODY$
	LANGUAGE plpgsql
	COST 100
	ROWS 1000
	CALLED ON NULL INPUT
	SECURITY INVOKER
	VOLATILE;
ALTER FUNCTION "bc_nearest_neighbor"(IN uname varchar, IN since timestamp, IN "k" int4) OWNER TO "postgres";

-- ----------------------------
--  Primary key structure for table "spatial_ref_sys"
-- ----------------------------
ALTER TABLE "spatial_ref_sys" ADD CONSTRAINT "spatial_ref_sys_pkey" PRIMARY KEY ("srid");

-- ----------------------------
--  Primary key structure for table "geometry_columns"
-- ----------------------------
ALTER TABLE "geometry_columns" ADD CONSTRAINT "geometry_columns_pk" PRIMARY KEY ("f_table_catalog", "f_table_schema", "f_table_name", "f_geometry_column");

-- ----------------------------
--  Primary key structure for table "privacy_default_list"
-- ----------------------------
ALTER TABLE "privacy_default_list" ADD CONSTRAINT "privacy_default_list_pkey" PRIMARY KEY ("username");

-- ----------------------------
--  Primary key structure for table "areacodes"
-- ----------------------------
ALTER TABLE "areacodes" ADD CONSTRAINT "areacodes_pkey" PRIMARY KEY ("area_id");

-- ----------------------------
--  Primary key structure for table "place_subscriptions"
-- ----------------------------
ALTER TABLE "place_subscriptions" ADD CONSTRAINT "place_subscriptions_pkey" PRIMARY KEY ("user_id", "place_id");

-- ----------------------------
--  Primary key structure for table "beacon_info_3rdparty"
-- ----------------------------
ALTER TABLE "beacon_info_3rdparty" ADD CONSTRAINT "query_beacons_cache_pkey" PRIMARY KEY ("beacon_id");

-- ----------------------------
--  Primary key structure for table "beacon_pattern_beacons"
-- ----------------------------
ALTER TABLE "beacon_pattern_beacons" ADD CONSTRAINT "beacon_pattern_beacons_pkey" PRIMARY KEY ("pattern_id", "beacon_id");

-- ----------------------------
--  Primary key structure for table "beacon_patterns"
-- ----------------------------
ALTER TABLE "beacon_patterns" ADD CONSTRAINT "beacon_patterns_pkey" PRIMARY KEY ("pattern_id");

-- ----------------------------
--  Primary key structure for table "comments"
-- ----------------------------
ALTER TABLE "comments" ADD CONSTRAINT "comments_pkey" PRIMARY KEY ("id");

-- ----------------------------
--  Primary key structure for table "help_messages_sent"
-- ----------------------------
ALTER TABLE "help_messages_sent" ADD CONSTRAINT "help_messages_sent_pkey" PRIMARY KEY ("id");

-- ----------------------------
--  Primary key structure for table "last"
-- ----------------------------
ALTER TABLE "last" ADD CONSTRAINT "last_pkey" PRIMARY KEY ("username");

-- ----------------------------
--  Primary key structure for table "location_history_backup"
-- ----------------------------
ALTER TABLE "location_history_backup" ADD CONSTRAINT "pk_location_history_backup" PRIMARY KEY ("history_id");

-- ----------------------------
--  Primary key structure for table "long_strings"
-- ----------------------------
ALTER TABLE "long_strings" ADD CONSTRAINT "long_strings_pkey" PRIMARY KEY ("id");

-- ----------------------------
--  Indexes structure for table "long_strings"
-- ----------------------------
CREATE INDEX "long_strings_lang_idx" ON "long_strings" USING btree(lang ASC NULLS LAST);
CREATE INDEX "long_strings_string_id_idx" ON "long_strings" USING btree(string_id ASC NULLS LAST);

-- ----------------------------
--  Primary key structure for table "place_overrides"
-- ----------------------------
ALTER TABLE "place_overrides" ADD CONSTRAINT "place_overrides_pkey" PRIMARY KEY ("override_id");

-- ----------------------------
--  Primary key structure for table "standards_iso_3166"
-- ----------------------------
ALTER TABLE "standards_iso_3166" ADD CONSTRAINT "standards_iso_3166_pkey" PRIMARY KEY ("iso");

-- ----------------------------
--  Primary key structure for table "beacon_observations"
-- ----------------------------
ALTER TABLE "beacon_observations" ADD CONSTRAINT "beacon_observations_pkey" PRIMARY KEY ("oid");

-- ----------------------------
--  Primary key structure for table "places"
-- ----------------------------
ALTER TABLE "places" ADD CONSTRAINT "places_pkey" PRIMARY KEY ("place_id");

-- ----------------------------
--  Primary key structure for table "country"
-- ----------------------------
ALTER TABLE "country" ADD CONSTRAINT "pk_country" PRIMARY KEY ("code");

-- ----------------------------
--  Primary key structure for table "continent"
-- ----------------------------
ALTER TABLE "continent" ADD CONSTRAINT "pk_continent" PRIMARY KEY ("code");

-- ----------------------------
--  Primary key structure for table "query_beacons"
-- ----------------------------
ALTER TABLE "query_beacons" ADD CONSTRAINT "query_beacons_pkey" PRIMARY KEY ("log_id", "beacon_id");

-- ----------------------------
--  Indexes structure for table "query_beacons"
-- ----------------------------
CREATE INDEX "query_beacons_beacon_id_idx" ON "query_beacons" USING btree(beacon_id ASC NULLS LAST);

-- ----------------------------
--  Primary key structure for table "location_users"
-- ----------------------------
ALTER TABLE "location_users" ADD CONSTRAINT "pk_location_users" PRIMARY KEY ("user_id");

-- ----------------------------
--  Primary key structure for table "location_history"
-- ----------------------------
ALTER TABLE "location_history" ADD CONSTRAINT "location_history_pkey" PRIMARY KEY ("history_id");

-- ----------------------------
--  Primary key structure for table "bcloud_moods"
-- ----------------------------
ALTER TABLE "bcloud_moods" ADD CONSTRAINT "bcloud_moods_pkey" PRIMARY KEY ("mood_id");

-- ----------------------------
--  Primary key structure for table "beacons"
-- ----------------------------
ALTER TABLE "beacons" ADD CONSTRAINT "beacons_pkey" PRIMARY KEY ("beacon_id");

-- ----------------------------
--  Primary key structure for table "current_locations"
-- ----------------------------
ALTER TABLE "current_locations" ADD CONSTRAINT "pk_current_locations" PRIMARY KEY ("user_id");

-- ----------------------------
--  Primary key structure for table "groups"
-- ----------------------------
ALTER TABLE "groups" ADD CONSTRAINT "groups_pkey" PRIMARY KEY ("id");

-- ----------------------------
--  Primary key structure for table "next_locations"
-- ----------------------------
ALTER TABLE "next_locations" ADD CONSTRAINT "next_locations_pkey" PRIMARY KEY ("next_location_id");

-- ----------------------------
--  Primary key structure for table "place_history"
-- ----------------------------
ALTER TABLE "place_history" ADD CONSTRAINT "pk_place_history" PRIMARY KEY ("user_id", "place_id", "entry_time");

-- ----------------------------
--  Primary key structure for table "queries"
-- ----------------------------
ALTER TABLE "queries" ADD CONSTRAINT "queries_pkey" PRIMARY KEY ("log_id");

-- ----------------------------
--  Primary key structure for table "thirdparty_users"
-- ----------------------------
ALTER TABLE "thirdparty_users" ADD CONSTRAINT "thirdparty_users_pkey" PRIMARY KEY ("id");
