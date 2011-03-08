/**
 * 
 */

package com.buddycloud.location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class encapsulating ISO 3166 Alpha 2 country codes and mappings to other codes as well
 * as country names in different languages.
 * 
 * Sources: http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
 * http://snippets.dzone.com/posts/show/1051
 * 
 * NOTE: This should perhaps be built on top of a (geonames?) database table, but awaiting
 * the existance of such, this will have to do
 * 
 * @author buddycloud
 * 
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public enum CountryCode {
   AD, AE, AF, AG, AI, AL, AM, AN, AO, AQ, AR, AS, AT, AU, AW, AX, AZ, BA, BB, BD, BE, BF, BG, BH, BI, BJ, BL, BM, BN, BO, BR, BS, BT, BV, BW, BY, BZ, CA, CC, CD, CF, CG, CH, CI, CK, CL, CM, CN, CO, CR, CU, CV, CX, CY, CZ, DE, DJ, DK, DM, DO, DZ, EC, EE, EG, EH, ER, ES, ET, FI, FJ, FK, FM, FO, FR, GA, GB, GD, GE, GF, GG, GH, GI, GL, GM, GN, GP, GQ, GR, GS, GT, GU, GW, GY, HK, HM, HN, HR, HT, HU, ID, IE, IL, IM, IN, IO, IQ, IR, IS, IT, JE, JM, JO, JP, KE, KG, KH, KI, KM, KN, KP, KR, KW, KY, KZ, LA, LB, LC, LI, LK, LR, LS, LT, LU, LV, LY, MA, MC, MD, ME, MF, MG, MH, MK, ML, MM, MN, MO, MP, MQ, MR, MS, MT, MU, MV, MW, MX, MY, MZ, NA, NC, NE, NF, NG, NI, NL, NO, NP, NR, NU, NZ, OM, PA, PE, PF, PG, PH, PK, PL, PM, PN, PR, PS, PT, PW, PY, QA, RE, RO, RS, RU, RW, SA, SB, SC, SD, SE, SG, SH, SI, SJ, SK, SL, SM, SN, SO, SR, ST, SV, SY, SZ, TC, TD, TF, TG, TH, TJ, TK, TL, TM, TN, TO, TR, TT, TV, TW, TZ, UA, UG, UM, US, UY, UZ, VA, VC, VE, VG, VI, VN, VU, WF, WS, YE, YT, ZA, ZM, ZW;

   public static CountryCode getInstance(String countryName){
      if(countryName == null){
      	return null;
      }
      String trimmedLowercaseName = countryName.trim().toLowerCase();
      if (nameToCodeMap.containsKey( trimmedLowercaseName ))
      {
         return nameToCodeMap.get( trimmedLowercaseName );
      }
      else
      {
         throw new IllegalArgumentException( "Country name '" + countryName
                                             + "' not known. Considder adding to "
                                             + CountryCode.class.getName()
                                             + ".nameToCodeMap" );
      }
   }
   
   /**
    * Returns true if the provided name is a known country name
    * @param countryName
    * @return
    */
   public static boolean isKnownCountryName(String countryName){
   	
   	if(countryName == null) return false;
   	try{
   		getInstance(countryName);
   		return true;
   	}
   	catch(Exception e){
   		return false;
   	}
   }
   
   /**
    * Returns the country code corresponding to the provided Mobile Country Code (MCC)
    * @param mobileCountryCode The mcc
    * @return The corresponding country code
    */
   public static CountryCode getInstanceFromMCC(int mobileCountryCode){
      CountryCode cc = mcc.get(mobileCountryCode);
      if(cc==null) throw new IllegalArgumentException("No country code known for MCC "+mobileCountryCode);
      return cc; 
   }


   /**
    * Country code to english map, from http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
    */
   private static Map<CountryCode, String> english;
   static
   {
      Map<CountryCode, String> en = new HashMap<CountryCode, String>();
      en.put( AD, "Andorra" );
      en.put( AE, "United Arab Emirates" );
      en.put( AF, "Afghanistan" );
      en.put( AG, "Antigua and Barbuda" );
      en.put( AI, "Anguilla" );
      en.put( AL, "Albania" );
      en.put( AM, "Armenia" );
      en.put( AN, "Netherlands Antilles" );
      en.put( AO, "Angola" );
      en.put( AQ, "Antarctica" );
      en.put( AR, "Argentina" );
      en.put( AS, "American Samoa" );
      en.put( AT, "Austria" );
      en.put( AU, "Australia" );
      en.put( AW, "Aruba" );
      en.put( AX, "Åland Islands" );
      en.put( AZ, "Azerbaijan" );
      en.put( BA, "Bosnia and Herzegovina" );
      en.put( BB, "Barbados" );
      en.put( BD, "Bangladesh" );
      en.put( BE, "Belgium" );
      en.put( BF, "Burkina Faso" );
      en.put( BG, "Bulgaria" );
      en.put( BH, "Bahrain" );
      en.put( BI, "Burundi" );
      en.put( BJ, "Benin" );
      en.put( BL, "Saint Barthélemy" );
      en.put( BM, "Bermuda" );
      en.put( BN, "Brunei Darussalam" );
      en.put( BO, "Bolivia" );
      en.put( BR, "Brazil" );
      en.put( BS, "Bahamas" );
      en.put( BT, "Bhutan" );
      en.put( BV, "Bouvet Island" );
      en.put( BW, "Botswana" );
      en.put( BY, "Belarus" );
      en.put( BZ, "Belize" );
      en.put( CA, "Canada" );
      en.put( CC, "Cocos (Keeling) Islands" );
      en.put( CD, "Democratic Republic of the Congo" );
      en.put( CF, "Central Africa" );
      en.put( CG, "Congo" );
      en.put( CH, "Switzerland" );
      en.put( CI, "Cote d'Ivoire" );
      en.put( CK, "Cook Islands" );
      en.put( CL, "Chile" );
      en.put( CM, "Cameroon" );
      en.put( CN, "China" );
      en.put( CO, "Colombia" );
      en.put( CR, "Costa Rica" );
      en.put( CU, "Cuba" );
      en.put( CV, "Cape Verde" );
      en.put( CX, "Christmas Island" );
      en.put( CY, "Cyprus" );
      en.put( CZ, "Czech Republic" );
      en.put( DE, "Germany" );
      en.put( DJ, "Djibouti" );
      en.put( DK, "Denmark" );
      en.put( DM, "Dominica" );
      en.put( DO, "Dominican Republic" );
      en.put( DZ, "Algeria" );
      en.put( EC, "Ecuador" );
      en.put( EE, "Estonia" );
      en.put( EG, "Egypt" );
      en.put( EH, "Western Sahara" );
      en.put( ER, "Eritrea" );
      en.put( ES, "Spain" );
      en.put( ET, "Ethiopia" );
      en.put( FI, "Finland" );
      en.put( FJ, "Fiji" );
      en.put( FK, "Falkland Islands" );
      en.put( FM, "Micronesia" );
      en.put( FO, "Faroe Islands" );
      en.put( FR, "France" );
      en.put( GA, "Gabon" );
      en.put( GB, "United Kingdom" );
      en.put( GD, "Grenada" );
      en.put( GE, "Georgia" );
      en.put( GF, "French Guiana" );
      en.put( GG, "Guernsey" );
      en.put( GH, "Ghana" );
      en.put( GI, "Gibraltar" );
      en.put( GL, "Greenland" );
      en.put( GM, "Gambia" );
      en.put( GN, "Guinea" );
      en.put( GP, "Guadeloupe" );
      en.put( GQ, "Equatorial Guinea" );
      en.put( GR, "Greece" );
      en.put( GS, "South Georgia and the South Sandwich Islands" );
      en.put( GT, "Guatemala" );
      en.put( GU, "Guam" );
      en.put( GW, "Guinea-Bissau" );
      en.put( GY, "Guyana" );
      en.put( HK, "Hong Kong" );
      en.put( HM, "Heard Island and McDonald Islands" );
      en.put( HN, "Honduras" );
      en.put( HR, "Croatia" );
      en.put( HT, "Haiti" );
      en.put( HU, "Hungary" );
      en.put( ID, "Indonesia" );
      en.put( IE, "Ireland" );
      en.put( IL, "Israel" );
      en.put( IM, "Isle of Man" );
      en.put( IN, "India" );
      en.put( IO, "British Indian Ocean Territory" );
      en.put( IQ, "Iraq" );
      en.put( IR, "Iran" );
      en.put( IS, "Iceland" );
      en.put( IT, "Italy" );
      en.put( JE, "Jersey" );
      en.put( JM, "Jamaica" );
      en.put( JO, "Jordan" );
      en.put( JP, "Japan" );
      en.put( KE, "Kenya" );
      en.put( KG, "Kyrgyzstan" );
      en.put( KH, "Cambodia" );
      en.put( KI, "Kiribati" );
      en.put( KM, "Comoros" );
      en.put( KN, "Saint Kitts and Nevis" );
      en.put( KP, "North Korea" );
      en.put( KR, "South Korea" );
      en.put( KW, "Kuwait" );
      en.put( KY, "Cayman Islands" );
      en.put( KZ, "Kazakhstan" );
      en.put( LA, "Laos" );
      en.put( LB, "Lebanon" );
      en.put( LC, "Saint Lucia" );
      en.put( LI, "Liechtenstein" );
      en.put( LK, "Sri Lanka" );
      en.put( LR, "Liberia" );
      en.put( LS, "Lesotho" );
      en.put( LT, "Lithuania" );
      en.put( LU, "Luxembourg" );
      en.put( LV, "Latvia" );
      en.put( LY, "Libya" );
      en.put( MA, "Morocco" );
      en.put( MC, "Monaco" );
      en.put( MD, "Moldova" );
      en.put( ME, "Montenegro" );
      en.put( MF, "French Saint Martin" );
      en.put( MG, "Madagascar" );
      en.put( MH, "Marshall Islands" );
      en.put( MK, "Macedonia" );
      en.put( ML, "Mali" );
      en.put( MM, "Myanmar" );
      en.put( MN, "Mongolia" );
      en.put( MO, "Macao" );
      en.put( MP, "Northern Mariana Islands" );
      en.put( MQ, "Martinique" );
      en.put( MR, "Mauritania" );
      en.put( MS, "Montserrat" );
      en.put( MT, "Malta" );
      en.put( MU, "Mauritius" );
      en.put( MV, "Maldives" );
      en.put( MW, "Malawi" );
      en.put( MX, "Mexico" );
      en.put( MY, "Malaysia" );
      en.put( MZ, "Mozambique" );
      en.put( NA, "Namibia" );
      en.put( NC, "New Caledonia" );
      en.put( NE, "Niger" );
      en.put( NF, "Norfolk Island" );
      en.put( NG, "Nigeria" );
      en.put( NI, "Nicaragua" );
      en.put( NL, "Netherlands" );
      en.put( NO, "Norway" );
      en.put( NP, "Nepal" );
      en.put( NR, "Nauru" );
      en.put( NU, "Niue" );
      en.put( NZ, "New Zealand" );
      en.put( OM, "Oman" );
      en.put( PA, "Panama" );
      en.put( PE, "Peru" );
      en.put( PF, "French Polynesia" );
      en.put( PG, "Papua New Guinea" );
      en.put( PH, "Philippines" );
      en.put( PK, "Pakistan" );
      en.put( PL, "Poland" );
      en.put( PM, "Saint Pierre and Miquelon" );
      en.put( PN, "Pitcairn" );
      en.put( PR, "Puerto Rico" );
      en.put( PS, "Palestine" );
      en.put( PT, "Portugal" );
      en.put( PW, "Palau" );
      en.put( PY, "Paraguay" );
      en.put( QA, "Qatar" );
      en.put( RE, "Reunion Réunion" );
      en.put( RO, "Romania" );
      en.put( RS, "Serbia" );
      en.put( RU, "Russia" );
      en.put( RW, "Rwanda" );
      en.put( SA, "Saudi Arabia" );
      en.put( SB, "Solomon Islands" );
      en.put( SC, "Seychelles" );
      en.put( SD, "Sudan" );
      en.put( SE, "Sweden" );
      en.put( SG, "Singapore" );
      en.put( SH, "Saint Helena" );
      en.put( SI, "Slovenia" );
      en.put( SJ, "Svalbard and Jan Mayen" );
      en.put( SK, "Slovakia" );
      en.put( SL, "Sierra Leone" );
      en.put( SM, "San Marino" );
      en.put( SN, "Senegal" );
      en.put( SO, "Somalia" );
      en.put( SR, "Suriname" );
      en.put( ST, "Sao Tome and Principe" );
      en.put( SV, "El Salvador" );
      en.put( SY, "Syria" );
      en.put( SZ, "Swaziland" );
      en.put( TC, "Turks and Caicos Islands" );
      en.put( TD, "Chad" );
      en.put( TF, "French Southern Territories" );
      en.put( TG, "Togo" );
      en.put( TH, "Thailand" );
      en.put( TJ, "Tajikistan" );
      en.put( TK, "Tokelau" );
      en.put( TL, "Timor-Leste" );
      en.put( TM, "Turkmenistan" );
      en.put( TN, "Tunisia" );
      en.put( TO, "Tonga" );
      en.put( TR, "Turkey" );
      en.put( TT, "Trinidad and Tobago" );
      en.put( TV, "Tuvalu" );
      en.put( TW, "Taiwan" );
      en.put( TZ, "Tanzania" );
      en.put( UA, "Ukraine" );
      en.put( UG, "Uganda" );
      en.put( UM, "US Minor Outlying Islands" );
      en.put( US, "USA" );
      en.put( UY, "Uruguay" );
      en.put( UZ, "Uzbekistan" );
      en.put( VA, "Vatican City" );
      en.put( VC, "Saint Vincent and the Grenadines" );
      en.put( VE, "Venezuela" );
      en.put( VG, "British Virgin Islands" );
      en.put( VI, "US Virgin Islands" );
      en.put( VN, "Viet Nam" );
      en.put( VU, "Vanuatu" );
      en.put( WF, "Wallis and Futuna" );
      en.put( WS, "Samoa" );
      en.put( YE, "Yemen" );
      en.put( YT, "Mayotte" );
      en.put( ZA, "South Africa" );
      en.put( ZM, "Zambia" );
      en.put( ZW, "Zimbabwe" );

      english = Collections.unmodifiableMap( en );

   }

   /**
    * Country code to German map, from http://snippets.dzone.com/posts/show/1051
    */
   private static Map<CountryCode, String> german;
   static
   {
      Map<CountryCode, String> de = new HashMap<CountryCode, String>();
      de.put( AF, "Afghanistan" );
      de.put( EG, "Ägypten" );
      de.put( AL, "Albanien" );
      de.put( DZ, "Algerien" );
      de.put( AD, "Andorra" );
      de.put( AO, "Angola" );
      de.put( AI, "Anguilla" );
      de.put( AQ, "Antarktis" );
      de.put( AG, "Antigua und Barbuda" );
      de.put( GQ, "Äquatorial Guinea" );
      de.put( AR, "Argentinien" );
      de.put( AM, "Armenien" );
      de.put( AW, "Aruba" );
      de.put( AZ, "Aserbaidschan" );
      de.put( ET, "äthiopien" );
      de.put( AU, "Australien" );
      de.put( BS, "Bahamas" );
      de.put( BH, "Bahrain" );
      de.put( BD, "Bangladesh" );
      de.put( BB, "Barbados" );
      de.put( BE, "Belgien" );
      de.put( BZ, "Belize" );
      de.put( BJ, "Benin" );
      de.put( BM, "Bermudas" );
      de.put( BT, "Bhutan" );
      de.put( MM, "Birma" );
      de.put( BO, "Bolivien" );
      de.put( BA, "Bosnien-Herzegowina" );
      de.put( BW, "Botswana" );
      de.put( BV, "Bouvet Inseln" );
      de.put( BR, "Brasilien" );
      de.put( IO, "Britisch-Indischer Ozean" );
      de.put( BN, "Brunei" );
      de.put( BG, "Bulgarien" );
      de.put( BF, "Burkina Faso" );
      de.put( BI, "Burundi" );
      de.put( CL, "Chile" );
      de.put( CN, "China" );
      de.put( CX, "Christmas Island" );
      de.put( CK, "Cook Inseln" );
      de.put( CR, "Costa Rica" );
      de.put( DK, "Dänemark" );
      de.put( DE, "Deutschland" );
      de.put( DJ, "Djibuti" );
      de.put( DM, "Dominika" );
      de.put( DO, "Dominikanische Republik" );
      de.put( EC, "Ecuador" );
      de.put( SV, "El Salvador" );
      de.put( CI, "Elfenbeinküste" );
      de.put( ER, "Eritrea" );
      de.put( EE, "Estland" );
      de.put( FK, "Falkland Inseln" );
      de.put( FO, "Färöer Inseln" );
      de.put( FJ, "Fidschi" );
      de.put( FI, "Finnland" );
      de.put( FR, "Frankreich" );
      de.put( GF, "französisch Guyana" );
      de.put( PF, "Französisch Polynesien" );
      de.put( TF, "Französisches Süd-Territorium" );
      de.put( GA, "Gabun" );
      de.put( GM, "Gambia" );
      de.put( GE, "Georgien" );
      de.put( GH, "Ghana" );
      de.put( GI, "Gibraltar" );
      de.put( GD, "Grenada" );
      de.put( GR, "Griechenland" );
      de.put( GL, "Grönland" );
      // de.put(UK, "Großbritannien");
      de.put( GB, "Großbritannien" );
      de.put( GP, "Guadeloupe" );
      de.put( GU, "Guam" );
      de.put( GT, "Guatemala" );
      de.put( GN, "Guinea" );
      de.put( GW, "Guinea Bissau" );
      de.put( GY, "Guyana" );
      de.put( HT, "Haiti" );
      de.put( HM, "Heard und McDonald Islands" );
      de.put( HN, "Honduras" );
      de.put( HK, "Hong Kong" );
      de.put( IN, "Indien" );
      de.put( ID, "Indonesien" );
      de.put( IQ, "Irak" );
      de.put( IR, "Iran" );
      de.put( IE, "Irland" );
      de.put( IS, "Island" );
      de.put( IL, "Israel" );
      de.put( IT, "Italien" );
      de.put( JM, "Jamaika" );
      de.put( JP, "Japan" );
      de.put( YE, "Jemen" );
      de.put( JO, "Jordanien" );
      // de.put(YU, "Jugoslawien");
      de.put( KY, "Kaiman Inseln" );
      de.put( KH, "Kambodscha" );
      de.put( CM, "Kamerun" );
      de.put( CA, "Kanada" );
      de.put( CV, "Kap Verde" );
      de.put( KZ, "Kasachstan" );
      de.put( KE, "Kenia" );
      de.put( KG, "Kirgisistan" );
      de.put( KI, "Kiribati" );
      de.put( CC, "Kokosinseln" );
      de.put( CO, "Kolumbien" );
      de.put( KM, "Komoren" );
      de.put( CG, "Kongo" );
      de.put( CD, "Kongo, Demokratische Republik" );
      de.put( HR, "Kroatien" );
      de.put( CU, "Kuba" );
      de.put( KW, "Kuwait" );
      de.put( LA, "Laos" );
      de.put( LS, "Lesotho" );
      de.put( LV, "Lettland" );
      de.put( LB, "Libanon" );
      de.put( LR, "Liberia" );
      de.put( LY, "Libyen" );
      de.put( LI, "Liechtenstein" );
      de.put( LT, "Litauen" );
      de.put( LU, "Luxemburg" );
      de.put( MO, "Macao" );
      de.put( MG, "Madagaskar" );
      de.put( MW, "Malawi" );
      de.put( MY, "Malaysia" );
      de.put( MV, "Malediven" );
      de.put( ML, "Mali" );
      de.put( MT, "Malta" );
      de.put( MP, "Marianen" );
      de.put( MA, "Marokko" );
      de.put( MH, "Marshall Inseln" );
      de.put( MQ, "Martinique" );
      de.put( MR, "Mauretanien" );
      de.put( MU, "Mauritius" );
      de.put( YT, "Mayotte" );
      de.put( MK, "Mazedonien" );
      de.put( MX, "Mexiko" );
      de.put( FM, "Mikronesien" );
      de.put( MZ, "Mocambique" );
      de.put( MD, "Moldavien" );
      de.put( MC, "Monaco" );
      de.put( MN, "Mongolei" );
      de.put( MS, "Montserrat" );
      de.put( NA, "Namibia" );
      de.put( NR, "Nauru" );
      de.put( NP, "Nepal" );
      de.put( NC, "Neukaledonien" );
      de.put( NZ, "Neuseeland" );
      de.put( NI, "Nicaragua" );
      de.put( NL, "Niederlande" );
      de.put( AN, "Niederländische Antillen" );
      de.put( NE, "Niger" );
      de.put( NG, "Nigeria" );
      de.put( NU, "Niue" );
      de.put( KP, "Nord Korea" );
      de.put( NF, "Norfolk Inseln" );
      de.put( NO, "Norwegen" );
      de.put( OM, "Oman" );
      de.put( AT, "österreich" );
      de.put( PK, "Pakistan" );
      de.put( PS, "Palästina" );
      de.put( PW, "Palau" );
      de.put( PA, "Panama" );
      de.put( PG, "Papua Neuguinea" );
      de.put( PY, "Paraguay" );
      de.put( PE, "Peru" );
      de.put( PH, "Philippinen" );
      de.put( PN, "Pitcairn" );
      de.put( PL, "Polen" );
      de.put( PT, "Portugal" );
      de.put( PR, "Puerto Rico" );
      de.put( QA, "Qatar" );
      de.put( RE, "Reunion" );
      de.put( RW, "Ruanda" );
      de.put( RO, "Rumänien" );
      de.put( RU, "Rußland" );
      de.put( LC, "Saint Lucia" );
      de.put( ZM, "Sambia" );
      de.put( AS, "Samoa" );
      de.put( WS, "Samoa" );
      de.put( SM, "San Marino" );
      de.put( ST, "Sao Tome" );
      de.put( SA, "Saudi Arabien" );
      de.put( SE, "Schweden" );
      de.put( CH, "Schweiz" );
      de.put( SN, "Senegal" );
      de.put( SC, "Seychellen" );
      de.put( SL, "Sierra Leone" );
      de.put( SG, "Singapur" );
      de.put( SK, "Slowakei -slowakische Republik-" );
      de.put( SI, "Slowenien" );
      de.put( SB, "Solomon Inseln" );
      de.put( SO, "Somalia" );
      de.put( GS, "South Georgia, South Sandwich Isl." );
      de.put( ES, "Spanien" );
      de.put( LK, "Sri Lanka" );
      de.put( SH, "St. Helena" );
      de.put( KN, "St. Kitts Nevis Anguilla" );
      de.put( PM, "St. Pierre und Miquelon" );
      de.put( VC, "St. Vincent" );
      de.put( KR, "Süd Korea" );
      de.put( ZA, "Südafrika" );
      de.put( SD, "Sudan" );
      de.put( SR, "Surinam" );
      de.put( SJ, "Svalbard und Jan Mayen Islands" );
      de.put( SZ, "Swasiland" );
      de.put( SY, "Syrien" );
      de.put( TJ, "Tadschikistan" );
      de.put( TW, "Taiwan" );
      de.put( TZ, "Tansania" );
      de.put( TH, "Thailand" );
      // de.put(TP, "Timor");
      de.put( TG, "Togo" );
      de.put( TK, "Tokelau" );
      de.put( TO, "Tonga" );
      de.put( TT, "Trinidad Tobago" );
      de.put( TD, "Tschad" );
      de.put( CZ, "Tschechische Republik" );
      de.put( TN, "Tunesien" );
      de.put( TR, "Türkei" );
      de.put( TM, "Turkmenistan" );
      de.put( TC, "Turks und Kaikos Inseln" );
      de.put( TV, "Tuvalu" );
      de.put( UG, "Uganda" );
      de.put( UA, "Ukraine" );
      de.put( HU, "Ungarn" );
      de.put( UY, "Uruguay" );
      de.put( UZ, "Usbekistan" );
      de.put( VU, "Vanuatu" );
      de.put( VA, "Vatikan" );
      de.put( VE, "Venezuela" );
      de.put( AE, "Vereinigte Arabische Emirate" );
      de.put( US, "Vereinigte Staaten von Amerika" );
      de.put( VN, "Vietnam" );
      de.put( VG, "Virgin Island (Brit.)" );
      de.put( VI, "Virgin Island (USA)" );
      de.put( WF, "Wallis et Futuna" );
      de.put( BY, "Weissrussland" );
      de.put( EH, "Westsahara" );
      de.put( CF, "Zentralafrikanische Republik" );
      de.put( ZW, "Zimbabwe" );
      de.put( CY, "Zypern" );

      german = Collections.unmodifiableMap( de );

   }

   /**
    * Country code to Norwegian map
    */
   private static Map<CountryCode, String> norwegian;
   static
   {
      Map<CountryCode, String> no = new HashMap<CountryCode, String>();
      no.put( NO, "Norge" );
      no.put( SE, "Sverige" );
      no.put( DK, "Danmark" );
      no.put( DE, "Tyskland" );
      no.put( FR, "Frankrike" );
      no.put( ES, "Spania" );
      no.put( GB, "Storbritannia" );
      no.put( RU, "Russland" );
      no.put( IT, "Italia" );
      no.put( IS, "Island" );
      no.put( BR, "Brasil" );
      norwegian = Collections.unmodifiableMap( no );

   }

   /**
    * Country code to english map, from http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
    */
   private static Map<String, CountryCode> nameToCodeMap;
   static
   {
      Map<String, CountryCode> n2c = new HashMap<String, CountryCode>();

      n2c.put( "united states", US );
      n2c.put( "united states of america", US );
      n2c.put( "uk", GB );
      n2c.put( "england", GB );
      n2c.put( "scotland", GB );
      n2c.put( "wales", GB );
      n2c.put( "hong kong (prc)", HK );
      n2c.put( "azerbaijani republic", AZ );
      n2c.put( "republic of macedonia", MK );
      n2c.put( "argentine republic", AR );
      n2c.put( "russian federation", RU );
      n2c.put( "Россия".toLowerCase(), RU );
      n2c.put( "kyrgyz republic", KG );
      n2c.put( "serbia and montenegro", RS );
      n2c.put( "holland", NL );
      n2c.put( "brasil", BR );
      n2c.put( "italia", IT );
      n2c.put( "hvratska", HR );
      n2c.put( "macedonia (fyrom)", MK );
      n2c.put( "byelarus", BY );
      n2c.put( "burma", MM );
      n2c.put( "myanmar (burma)", MM );
      n2c.put( "대한민국", KR ) ;
      n2c.put( "españa", ES );
      n2c.put( "Česká republika".toLowerCase(), CZ );
      n2c.put( "méxico", MX );
      n2c.put( "Ελλάδα".toLowerCase(), GR );
      
      for (CountryCode cc : english.keySet())
      {
         String name = english.get( cc ).toLowerCase();
         if (!n2c.containsKey( name ))
         {
            n2c.put( name, cc );
         }
      }

      // Add string representation of the codes themselves
      for (CountryCode cc : CountryCode.values())
      {
         n2c.put( cc.toString().toLowerCase(), cc );
      }

      // Add English country names
      for (CountryCode cc : german.keySet())
      {
         String name = german.get( cc ).toLowerCase();
         if (!n2c.containsKey( name ))
         {
            n2c.put( name, cc );
         }
      }

      // Add Norwegian country names
      for (CountryCode cc : norwegian.keySet())
      {
         String name = norwegian.get( cc ).toLowerCase();
         if (!n2c.containsKey( name ))
         {
            n2c.put( name, cc );
         }
      }

      // Add German country names (if not already added)
      for (CountryCode cc : german.keySet())
      {
         String name = german.get( cc ).toLowerCase();
         if (!n2c.containsKey( name ))
         {
            n2c.put( name, cc );
         }
      }

      nameToCodeMap = Collections.unmodifiableMap( n2c );

   }

   private static Map<Integer, CountryCode> mcc;
   static
   {
      mcc = new HashMap<Integer, CountryCode>();
      mcc.put( 289, GE );
      mcc.put( 412, AF );
      mcc.put( 276, AL );
      mcc.put( 603, DZ );
      mcc.put( 544, AS );
      mcc.put( 213, AD );
      mcc.put( 631, AO );
      mcc.put( 365, AI );
      mcc.put( 344, AG );
      mcc.put( 722, AR );
      mcc.put( 283, AM );
      mcc.put( 363, AW );
      mcc.put( 505, AU );
      mcc.put( 232, AT );
      mcc.put( 400, AZ );
      mcc.put( 364, BS );
      mcc.put( 426, BH );
      mcc.put( 470, BD );
      mcc.put( 342, BB );
      mcc.put( 257, BY );
      mcc.put( 206, BE );
      mcc.put( 702, BZ );
      mcc.put( 616, BJ );
      mcc.put( 350, BM );
      mcc.put( 402, BT );
      mcc.put( 736, BO );
      mcc.put( 218, BA );
      mcc.put( 652, BW );
      mcc.put( 724, BR );
      mcc.put( 348, VG );
      mcc.put( 528, BN );
      mcc.put( 284, BG );
      mcc.put( 613, BF );
      mcc.put( 642, BI );
      mcc.put( 456, KH );
      mcc.put( 624, CM );
      mcc.put( 302, CA );
      mcc.put( 625, CV );
      mcc.put( 346, KY );
      mcc.put( 623, CF );
      mcc.put( 622, TD );
      mcc.put( 730, CL );
      mcc.put( 460, CN );
      mcc.put( 732, CO );
      mcc.put( 654, KM );
      mcc.put( 629, CG );
      mcc.put( 548, CK );
      mcc.put( 712, CR );
      mcc.put( 612, CI );
      mcc.put( 219, HR );
      mcc.put( 368, CU );
      mcc.put( 280, CY );
      mcc.put( 230, CZ );
      mcc.put( 630, CD );
      mcc.put( 238, DK );
      mcc.put( 638, DJ );
      mcc.put( 366, DM );
      mcc.put( 370, DO );
      mcc.put( 514, TL );
      mcc.put( 740, EC );
      mcc.put( 602, EG );
      mcc.put( 706, SV );
      mcc.put( 627, GQ );
      mcc.put( 657, ER );
      mcc.put( 248, EE );
      mcc.put( 636, ET );
      mcc.put( 288, FO );
      mcc.put( 542, FJ );
      mcc.put( 244, FI );
      mcc.put( 208, FR );
      mcc.put( 742, GF );
      mcc.put( 547, PF );
      mcc.put( 628, GA );
      mcc.put( 607, GM );
      mcc.put( 282, GE );
      mcc.put( 262, DE );
      mcc.put( 620, GH );
      mcc.put( 266, GI );
      mcc.put( 202, GR );
      mcc.put( 290, GL );
      mcc.put( 352, GD );
      mcc.put( 340, GU );
      mcc.put( 535, GU );
      mcc.put( 704, GT );
      mcc.put( 611, GN );
      mcc.put( 632, GW );
      mcc.put( 738, GY );
      mcc.put( 372, HT );
      mcc.put( 708, HN );
      mcc.put( 454, HK );
      mcc.put( 216, HU );
      mcc.put( 274, IS );
      mcc.put( 404, IN );
      mcc.put( 405, IN );
      mcc.put( 510, ID );
      mcc.put( 432, IR );
      mcc.put( 418, IQ );
      mcc.put( 272, IE );
      mcc.put( 425, IL );
      mcc.put( 222, IT );
      mcc.put( 338, JM );
      mcc.put( 441, JP );
      mcc.put( 440, JP );
      mcc.put( 416, JO );
      mcc.put( 401, KZ );
      mcc.put( 639, KE );
      mcc.put( 545, KI );
      mcc.put( 467, KP );
      mcc.put( 450, KR );
      mcc.put( 419, KW );
      mcc.put( 437, KG );
      mcc.put( 457, LA );
      mcc.put( 247, LV );
      mcc.put( 415, LB );
      mcc.put( 651, LS );
      mcc.put( 618, LR );
      mcc.put( 606, LY );
      mcc.put( 295, LI );
      mcc.put( 246, LT );
      mcc.put( 270, LU );
      mcc.put( 455, MO );
      mcc.put( 294, MK );
      mcc.put( 646, MG );
      mcc.put( 650, MW );
      mcc.put( 502, MY );
      mcc.put( 472, MV );
      mcc.put( 610, ML );
      mcc.put( 278, MT );
      mcc.put( 551, MH );
      mcc.put( 340, MQ );
      mcc.put( 609, MR );
      mcc.put( 617, MU );
      mcc.put( 334, MX );
      mcc.put( 550, FM );
      mcc.put( 259, MD );
      mcc.put( 212, MC );
      mcc.put( 428, MN );
      mcc.put( 297, ME );
      mcc.put( 354, MS );
      mcc.put( 604, MA );
      mcc.put( 643, MZ );
      mcc.put( 414, MM );
      mcc.put( 649, NA );
      mcc.put( 536, NR );
      mcc.put( 429, NP );
      mcc.put( 204, NL );
      mcc.put( 362, AN );
      mcc.put( 546, NC );
      mcc.put( 530, NZ );
      mcc.put( 710, NI );
      mcc.put( 614, NE );
      mcc.put( 621, NG );
      mcc.put( 534, MP );
      mcc.put( 242, NO );
      mcc.put( 422, OM );
      mcc.put( 410, PK );
      mcc.put( 552, PW );
      mcc.put( 423, PS );
      mcc.put( 714, PA );
      mcc.put( 537, PG );
      mcc.put( 744, PY );
      mcc.put( 716, PE );
      mcc.put( 515, PH );
      mcc.put( 260, PL );
      mcc.put( 268, PT );
      mcc.put( 330, PR );
      mcc.put( 427, QA );
      mcc.put( 647, RE );
      mcc.put( 226, RO );
      mcc.put( 250, RU );
      mcc.put( 635, RW );
      mcc.put( 356, KN );
      mcc.put( 358, LC );
      mcc.put( 308, PM );
      mcc.put( 360, VC );
      mcc.put( 549, WS );
      mcc.put( 292, SM );
      mcc.put( 626, ST );
      mcc.put( 420, SA );
      mcc.put( 608, SN );
      mcc.put( 220, RS );
      mcc.put( 633, SC );
      mcc.put( 619, SL );
      mcc.put( 525, SG );
      mcc.put( 231, SK );
      mcc.put( 293, SI );
      mcc.put( 540, SB );
      mcc.put( 637, SO );
      mcc.put( 655, ZA );
      mcc.put( 214, ES );
      mcc.put( 413, LK );
      mcc.put( 634, SD );
      mcc.put( 746, SR );
      mcc.put( 653, SZ );
      mcc.put( 240, SE );
      mcc.put( 228, CH );
      mcc.put( 417, SY );
      mcc.put( 466, TW );
      mcc.put( 436, TJ );
      mcc.put( 640, TZ );
      mcc.put( 520, TH );
      mcc.put( 615, TG );
      mcc.put( 539, TO );
      mcc.put( 374, TT );
      mcc.put( 605, TN );
      mcc.put( 286, TR );
      mcc.put( 438, TM );
      mcc.put( 376, TC );
      mcc.put( 641, UG );
      mcc.put( 255, UA );
      mcc.put( 424, AE );
      mcc.put( 430, AE );
      mcc.put( 431, AE );
      mcc.put( 235, GB );
      mcc.put( 234, GB );
      mcc.put( 310, US );
      mcc.put( 311, US );
      mcc.put( 312, US );
      mcc.put( 313, US );
      mcc.put( 314, US );
      mcc.put( 315, US );
      mcc.put( 316, US );
      mcc.put( 332, VI );
      mcc.put( 748, UY );
      mcc.put( 434, UZ );
      mcc.put( 541, VU );
      mcc.put( 225, VA );
      mcc.put( 734, VE );
      mcc.put( 452, VN );
      mcc.put( 543, WF );
      mcc.put( 421, YE );
      mcc.put( 645, ZM );
      mcc.put( 648, ZW );
   }


   /**
    * Returns the English name of the country this country code represents
    * 
    * @return the English country name
    */
   public String getEnglishCountryName()
   {
      return english.get( this );
   }


   /**
    * Returns the German name of the country this country code represents
    * 
    * @return the German country name
    */
   public String getGermanCountryName()
   {
      return german.get( this );
   }


   /**
    * Returns the ISO-3166 representation of this country code
    * 
    * @return the ISO-3166 code
    */
   public String toISO3166()
   {
      return toString();
   }

   /**
    * @param locale
    * @return
    */
   public String getCountryName(Locale locale)
   {
      if(locale == Locale.GERMAN || locale == Locale.GERMANY){
         return getGermanCountryName();
      }
      else{
         return getEnglishCountryName();
      }
   }
   
   public static void main(String[] args){
      for(String c : nameToCodeMap.keySet()){
         System.out.println(c+" = "+nameToCodeMap.get( c ));
      }
   }

}
