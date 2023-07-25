/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.currency;

/**
 * http://www.iso.org/iso/en/prods-services/popstds/currencycodeslist.html
 * 
 */
public class Iso4217 {

	public static final int COLUMN_ENTITY = 0;

	public static final int COLUMN_CURRENCY = 1;

	public static final int COLUMN_ALPHABETIC = 2;

	public static final int COLUMN_NUMERIC = 3;

	protected static final String[][] TABLE = new String[][] {
			// Entity, Currency, Alphabetic-Code, Numeric-Code
			{ "AFGHANISTAN", "Afghani", "AFN", "971" }, { "ALBANIA", "Lek", "ALL", "008" },
			{ "ALGERIA", "Algerian Dinar", "DZD", "012" }, { "AMERICAN SAMOA", "US Dollar", "USD", "840" },
			{ "ANDORRA", "Euro", "EUR", "978" }, { "ANGOLA", "Kwanza", "AOA", "973" },
			{ "ANGUILLA", "East Caribbean Dollar", "XCD", "951" },
			{ "ANTIGUA AND BARBUDA", "East Caribbean Dollar", "XCD", "951" },
			{ "ARGENTINA", "Argentine Peso", "ARS", "032" }, { "ARMENIA", "Armenian Dram", "AMD", "051" },
			{ "ARUBA", "Aruban Guilder", "AWG", "533" }, { "AUSTRALIA", "Australian Dollar", "AUD", "036" },
			{ "AUSTRIA", "Euro", "EUR", "978" }, { "AZERBAIJAN", "Azerbaijanian Manat", "AZN", "944" },
			{ "BAHAMAS", "Bahamian Dollar", "BSD", "044" }, { "BAHRAIN", "Bahraini Dinar", "BHD", "048" },
			{ "BANGLADESH", "Taka", "BDT", "050" }, { "BARBADOS", "Barbados Dollar", "BBD", "052" },
			{ "BELARUS", "Belarussian Ruble", "BYR", "974" }, { "BELGIUM", "Euro", "EUR", "978" },
			{ "BELIZE", "Belize Dollar", "BZD", "084" }, { "BENIN", "CFA Franc BCEAO", "XOF", "952" },
			{ "BERMUDA", "Bermudian Dollar", "BMD", "060" }, { "BHUTAN", "Indian Rupee", "INR", "356" },
			{ "BHUTAN", "Ngultrum", "BTN", "064" }, { "BOLIVIA", "Boliviano", "BOB", "068" },
			{ "BOLIVIA", "Mvdol", "BOV", "984" }, { "BOSNIA & HERZEGOVINA", "Convertible Marks", "BAM", "977" },
			{ "BOTSWANA", "Pula", "BWP", "072" }, { "BOUVET ISLAND", "Norwegian Krone", "NOK", "578" },
			{ "BRAZIL", "Brazilian Real", "BRL", "986" },
			{ "BRITISH INDIAN OCEAN TERRITORY", "US Dollar", "USD", "840" },
			{ "BRUNEI DARUSSALAM", "Brunei Dollar", "BND", "096" }, { "BULGARIA", "Bulgarian Lev", "BGN", "975" },
			{ "BURKINA FASO", "CFA Franc BCEAO", "XOF", "952" }, { "BURUNDI", "Burundi Franc", "BIF", "108" },
			{ "CAMBODIA", "Riel", "KHR", "116" }, { "CAMEROON", "CFA Franc BEAC", "XAF", "950" },
			{ "CANADA", "Canadian Dollar", "CAD", "124" }, { "CAPE VERDE", "Cape Verde Escudo", "CVE", "132" },
			{ "CAYMAN ISLANDS", "Cayman Islands Dollar", "KYD", "136" },
			{ "CENTRAL AFRICAN REPUBLIC", "CFA Franc BEAC", "XAF", "950" }, { "CHAD", "CFA Franc BEAC", "XAF", "950" },
			{ "CHILE", "Chilean Peso", "CLP", "152" }, { "CHILE", "Unidades de formento", "CLF", "990" },
			{ "CHINA", "Yuan Renminbi", "CNY", "156" }, { "CHRISTMAS ISLAND", "Australian Dollar", "AUD", "036" },
			{ "COCOS (KEELING) ISLANDS", "Australian Dollar", "AUD", "036" },
			{ "COLOMBIA", "Colombian Peso", "COP", "170" }, { "COLOMBIA", "Unidad de Valor Real", "COU", "970" },
			{ "COMOROS", "Comoro Franc", "KMF", "174" }, { "CONGO", "CFA Franc BEAC", "XAF", "950" },
			{ "CONGO THE DEMOCRATIC REPUBLIC OF", "Franc Congolais", "CDF", "976" },
			{ "COOK ISLANDS", "New Zealand Dollar", "NZD", "554" }, { "COSTA RICA", "Costa Rican Colon", "CRC", "188" },
			{ "C&Ocirc;TE D'IVOIRE", "CFA Franc BCEAO", "XOF", "952" }, { "CROATIA", "Croatian Kuna", "HRK", "191" },
			{ "CUBA", "Cuban Peso", "CUP", "192" }, { "CYPRUS", "Cyprus Pound", "CYP", "196" },
			{ "CZECH REPUBLIC", "Czech Koruna", "CZK", "203" }, { "DENMARK", "Danish Krone", "DKK", "208" },
			{ "DJIBOUTI", "Djibouti Franc", "DJF", "262" }, { "DOMINICA", "East Caribbean Dollar", "XCD", "951" },
			{ "DOMINICAN REPUBLIC", "Dominican Peso", "DOP", "214" }, { "ECUADOR", "US Dollar", "USD", "840" },
			{ "EGYPT", "Egyptian Pound", "EGP", "818" }, { "EL SALVADOR", "El Salvador Colon", "SVC", "222" },
			{ "EL SALVADOR", "US Dollar", "USD", "840" }, { "EQUATORIAL GUINEA", "CFA Franc BEAC", "XAF", "950" },
			{ "ERITREA", "Nakfa", "ERN", "232" }, { "ESTONIA", "Kroon", "EEK", "233" },
			{ "ETHIOPIA", "Ethiopian Birr", "ETB", "230" },
			{ "FALKLAND ISLANDS (MALVINAS)", "Falkland Islands Pound", "FKP", "238" },
			{ "FAROE ISLANDS", "Danish Krone", "DKK", "208" }, { "FIJI", "Fiji Dollar", "FJD", "242" },
			{ "FINLAND", "Euro", "EUR", "978" }, { "FRANCE", "Euro", "EUR", "978" },
			{ "FRENCH GUIANA", "Euro", "EUR", "978" }, { "FRENCH POLYNESIA", "CFP Franc", "XPF", "953" },
			{ "FRENCH SOUTHERN TERRITORIES", "Euro", "EUR", "978" }, { "GABON", "CFA Franc BEAC", "XAF", "950" },
			{ "GAMBIA", "Dalasi", "GMD", "270" }, { "GEORGIA", "Lari", "GEL", "981" },
			{ "GERMANY", "Euro", "EUR", "978" }, { "GHANA", "Cedi", "GHC", "288" },
			{ "GIBRALTAR", "Gibraltar Pound", "GIP", "292" }, { "GREECE", "Euro", "EUR", "978" },
			{ "GREENLAND", "Danish Krone", "DKK", "208" }, { "GRENADA", "East Caribbean Dollar", "XCD", "951" },
			{ "GUADELOUPE", "Euro", "EUR", "978" }, { "GUAM", "US Dollar", "USD", "840" },
			{ "GUATEMALA", "Quetzal", "GTQ", "320" }, { "GUINEA", "Guinea Franc", "GNF", "324" },
			{ "GUINEA-BISSAU", "Guinea-Bissau Peso", "GWP", "624" },
			{ "GUINEA-BISSAU", "CFA Franc BCEAO", "XOF", "952" }, { "GUYANA", "Guyana Dollar", "GYD", "328" },
			{ "HAITI", "Gourde", "HTG", "332" }, { "HAITI", "US Dollar", "USD", "840" },
			{ "HEARD ISLAND AND McDONALD ISLANDS", "Australian Dollar", "AUD", "036" },
			{ "HOLY SEE (VATICAN CITY STATE)", "Euro", "EUR", "978" }, { "HONDURAS", "Lempira", "HNL", "340" },
			{ "HONG KONG", "Hong Kong Dollar", "HKD", "344" }, { "HUNGARY", "Forint", "HUF", "348" },
			{ "ICELAND", "Iceland Krona", "ISK", "352" }, { "INDIA", "Indian Rupee", "INR", "356" },
			{ "INDONESIA", "Rupiah", "IDR", "360" }, { "INTERNATIONAL MONETARY FUND (I.M.F)", "SDR", "XDR", "960" },
			{ "IRAN (ISLAMIC REPUBLIC OF)", "Iranian Rial", "IRR", "364" }, { "IRAQ", "Iraqi Dinar", "IQD", "368" },
			{ "IRELAND", "Euro", "EUR", "978" }, { "ISRAEL", "New Israeli Sheqel", "ILS", "376" },
			{ "ITALY", "Euro", "EUR", "978" }, { "JAMAICA", "Jamaican Dollar", "JMD", "388" },
			{ "JAPAN", "Yen", "JPY", "392" }, { "JORDAN", "Jordanian Dinar", "JOD", "400" },
			{ "KAZAKHSTAN", "Tenge", "KZT", "398" }, { "KENYA", "Kenyan Shilling", "KES", "404" },
			{ "KIRIBATI", "Australian Dollar", "AUD", "036" },
			{ "KOREA DEMOCRATIC PEOPLE'S REPUBLIC OF", "North Korean Won", "KPW", "408" },
			{ "KOREA REPUBLIC OF", "Won", "KRW", "410" }, { "KUWAIT", "Kuwaiti Dinar", "KWD", "414" },
			{ "KYRGYZSTAN", "Som", "KGS", "417" }, { "LAO PEOPLE'S DEMOCRATIC REPUBLIC", "Kip", "LAK", "418" },
			{ "LATVIA", "Latvian Lats", "LVL", "428" }, { "LEBANON", "Lebanese Pound", "LBP", "422" },
			{ "LESOTHO", "Rand", "ZAR", "710" }, { "LESOTHO", "Loti", "LSL", "426" },
			{ "LIBERIA", "Liberian Dollar", "LRD", "430" }, { "LIBYAN ARAB JAMAHIRIYA", "Libyan Dinar", "LYD", "434" },
			{ "LIECHTENSTEIN", "Swiss Franc", "CHF", "756" }, { "LITHUANIA", "Lithuanian Litas", "LTL", "440" },
			{ "LUXEMBOURG", "Euro", "EUR", "978" }, { "MACAO", "Pataca", "MOP", "446" },
			{ "MACEDONIA THE FORMER YUGOSLAV REPUBLIC OF", "Denar", "MKD", "807" },
			{ "MADAGASCAR", "Malagascy Ariary", "MGA", "969" }, { "MALAWI", "Kwacha", "MWK", "454" },
			{ "MALAYSIA", "Malaysian Ringgit", "MYR", "458" }, { "MALDIVES", "Rufiyaa", "MVR", "462" },
			{ "MALI", "CFA Franc BCEAO", "XOF", "952" }, { "MALTA", "Maltese Lira", "MTL", "470" },
			{ "MARSHALL ISLANDS", "US Dollar", "USD", "840" }, { "MARTINIQUE", "Euro", "EUR", "978" },
			{ "MAURITANIA", "Ouguiya", "MRO", "478" }, { "MAURITIUS", "Mauritius Rupee", "MUR", "480" },
			{ "MAYOTTE", "Euro", "EUR", "978" }, { "MEXICO", "Mexican Peso", "MXN", "484" },
			{ "MEXICO", "Mexican Unidad de Inversion (UID)", "MXV", "979" },
			{ "MICRONESIA (FEDERATED STATES OF)", "US Dollar", "USD", "840" },
			{ "MOLDOVA REPUBLIC OF", "Moldovan Leu", "MDL", "498" }, { "MONACO", "Euro", "EUR", "978" },
			{ "MONGOLIA", "Tugrik", "MNT", "496" }, { "MONTENEGRO", "Euro", "EUR", "978" },
			{ "MONTSERRAT", "East Caribbean Dollar", "XCD", "951" }, { "MOROCCO", "Moroccan Dirham", "MAD", "504" },
			{ "MOZAMBIQUE", "Metical", "MZN", "943" }, { "MYANMAR", "Kyat", "MMK", "104" },
			{ "NAMIBIA", "Rand", "ZAR", "710" }, { "NAMIBIA", "Namibian Dollar", "NAD", "516" },
			{ "NAURU", "Australian Dollar", "AUD", "036" }, { "NEPAL", "Nepalese Rupee", "NPR", "524" },
			{ "NETHERLANDS", "Euro", "EUR", "978" },
			{ "NETHERLANDS ANTILLES", "Netherlands Antillian Guilder", "ANG", "532" },
			{ "NEW CALEDONIA", "CFP Franc", "XPF", "953" }, { "NEW ZEALAND", "New Zealand Dollar", "NZD", "554" },
			{ "NICARAGUA", "Cordoba Oro", "NIO", "558" }, { "NIGER", "CFA Franc BCEAO", "XOF", "952" },
			{ "NIGERIA", "Naira", "NGN", "566" }, { "NIUE", "New Zealand Dollar", "NZD", "554" },
			{ "NORFOLK ISLAND", "Australian Dollar", "AUD", "036" },
			{ "NORTHERN MARIANA ISLANDS", "US Dollar", "USD", "840" }, { "NORWAY", "Norwegian Krone", "NOK", "578" },
			{ "OMAN", "Rial Omani", "OMR", "512" }, { "PAKISTAN", "Pakistan Rupee", "PKR", "586" },
			{ "PALAU", "US Dollar", "USD", "840" }, { "PANAMA", "Balboa", "PAB", "590" },
			{ "PANAMA", "US Dollar", "USD", "840" }, { "PAPUA NEW GUINEA", "Kina", "PGK", "598" },
			{ "PARAGUAY", "Guarani", "PYG", "600" }, { "PERU", "Nuevo Sol", "PEN", "604" },
			{ "PHILIPPINES", "Philippine Peso", "PHP", "608" }, { "PITCAIRN", "New Zealand Dollar", "NZD", "554" },
			{ "POLAND", "Zloty", "PLN", "985" }, { "PORTUGAL", "Euro", "EUR", "978" },
			{ "PUERTO RICO", "US Dollar", "USD", "840" }, { "QATAR", "Qatari Rial", "QAR", "634" },
			{ "R&Eacute;UNION", "Euro", "EUR", "978" }, { "ROMANIA", "Old Leu", "ROL", "642" },
			{ "ROMANIA", "New Leu", "RON", "946" }, { "RUSSIAN FEDERATION", "Russian Ruble", "RUB", "643" },
			{ "RWANDA", "Rwanda Franc", "RWF", "646" }, { "SAINT HELENA", "Saint Helena Pound", "SHP", "654" },
			{ "SAINT KITTS AND NEVIS", "East Caribbean Dollar", "XCD", "951" },
			{ "SAINT LUCIA", "East Caribbean Dollar", "XCD", "951" },
			{ "SAINT PIERRE AND MIQUELON", "Euro", "EUR", "978" },
			{ "SAINT VINCENT AND THE GRENADINES", "East Caribbean Dollar", "XCD", "951" },
			{ "SAMOA", "Tala", "WST", "882" }, { "SAN MARINO", "Euro", "EUR", "978" },
			{ "S&Atilde;O TOME AND PRINCIPE", "Dobra", "STD", "678" }, { "SAUDI ARABIA", "Saudi Riyal", "SAR", "682" },
			{ "SENEGAL", "CFA Franc BCEAO", "XOF", "952" }, { "SERBIA", "Serbian Dinar", "RSD", "941" },
			{ "SEYCHELLES", "Seychelles Rupee", "SCR", "690" }, { "SIERRA LEONE", "Leone", "SLL", "694" },
			{ "SINGAPORE", "Singapore Dollar", "SGD", "702" }, { "SLOVAKIA", "Slovak Koruna", "SKK", "703" },
			{ "SLOVENIA", "Tolar", "SIT", "705" }, { "SOLOMON ISLANDS", "Solomon Islands Dollar", "SBD", "090" },
			{ "SOMALIA", "Somali Shilling", "SOS", "706" }, { "SOUTH AFRICA", "Rand", "ZAR", "710" },
			{ "SPAIN", "Euro", "EUR", "978" }, { "SRI LANKA", "Sri Lanka Rupee", "LKR", "144" },
			{ "SUDAN", "Sudanese Dinar", "SDG", "938" }, { "SURINAME", "Surinam Dollar", "SRD", "968" },
			{ "SVALBARD AND JAN MAYEN", "Norwegian Krone", "NOK", "578" }, { "SWAZILAND", "Lilangeni", "SZL", "748" },
			{ "SWEDEN", "Swedish Krona", "SEK", "752" }, { "SWITZERLAND", "Swiss Franc", "CHF", "756" },
			{ "SWITZERLAND", "WIR Franc", "CHW", "948" }, { "SWITZERLAND", "WIR Euro", "CHE", "947" },
			{ "SYRIAN ARAB REPUBLIC", "Syrian Pound", "SYP", "760" },
			{ "TAIWAN PROVINCE OF CHINA", "New Taiwan Dollar", "TWD", "901" }, { "TAJIKISTAN", "Somoni", "TJS", "972" },
			{ "TANZANIA UNITED REPUBLIC OF", "Tanzanian Shilling", "TZS", "834" }, { "THAILAND", "Baht", "THB", "764" },
			{ "TIMOR-LESTE", "US Dollar", "USD", "840" }, { "TOGO", "CFA Franc BCEAO", "XOF", "952" },
			{ "TOKELAU", "New Zealand Dollar", "NZD", "554" }, { "TONGA", "Pa'anga", "TOP", "776" },
			{ "TRINIDAD AND TOBAGO", "Trinidad and Tobago Dollar", "TTD", "780" },
			{ "TUNISIA", "Tunisian Dinar", "TND", "788" }, { "TURKEY", "New Turkish Lira", "TRY", "949" },
			{ "TURKMENISTAN", "Manat", "TMM", "795" }, { "TURKS AND CAICOS ISLANDS", "US Dollar", "USD", "840" },
			{ "TUVALU", "Australian Dollar", "AUD", "036" }, { "UGANDA", "Uganda Shilling", "UGX", "800" },
			{ "UKRAINE", "Hryvnia", "UAH", "980" }, { "UNITED ARAB EMIRATES", "UAE Dirham", "AED", "784" },
			{ "UNITED KINGDOM", "Pound Sterling", "GBP", "826" }, { "UNITED STATES", "US Dollar", "USD", "840" },
			{ "UNITED STATES", "US Dollar (Same day)", "USS", "998" },
			{ "UNITED STATES", "US Dollar(Next day)", "USN", "997" },
			{ "UNITED STATES MINOR OUTLYING ISLANDS", "US Dollar", "USD", "840" },
			{ "URUGUAY", "Peso Uruguayo", "UYU", "858" },
			{ "URUGUAY", "Uruguay Peso en Unidades Indexadas", "UYI", "940" },
			{ "UZBEKISTAN", "Uzbekistan Sum", "UZS", "860" }, { "VANUATU", "Vatu", "VUV", "548" },
			{ "VENEZUELA", "Bolivar", "VEB", "862" }, { "VIET NAM", "Dong", "VND", "704" },
			{ "VIRGIN ISLANDS (BRITISH)", "US Dollar", "USD", "840" },
			{ "VIRGIN ISLANDS (US)", "US Dollar", "USD", "840" }, { "WALLIS AND FUTUNA", "CFP Franc", "XPF", "953" },
			{ "WESTERN SAHARA", "Moroccan Dirham", "MAD", "504" }, { "YEMEN", "Yemeni Rial", "YER", "886" },
			{ "ZAMBIA", "Kwacha", "ZMK", "894" }, { "ZIMBABWE", "Zimbabwe Dollar", "ZWD", "716" },
			{ "-", "Gold", "XAU", "959" }, { "-", "Bond Markets Units European Composite Unit (EURCO)", "XBA", "955" },
			{ "-", "European Monetary Unit (E.M.U.-6)", "XBB", "956" },
			{ "-", "European Unit of Account 9(E.U.A.-9)", "XBC", "957" },
			{ "-", "European Unit of Account 17(E.U.A.-17)", "XBD", "958" }, { "-", "Palladium", "XPD", "964" },
			{ "-", "Platinum", "XPT", "962" }, { "-", "Silver", "XAG", "961" }, { "-", "UIC-Franc", "XFU", "-1" },
			{ "-", "Gold-Franc", "XFO", "-1" },
			{ "-", "Codes specifically reserved for testing purposes", "XTS", "963" },
			{ "-", "The codes assigned for transactions where no currency is involved are:", "XXX", "999" } };

	public static String Numeric2Alphabetic(int numeric) { // NOSONAR
		for (int row = 0; row < TABLE.length; row++) {
			String numericStr = TABLE[row][COLUMN_NUMERIC];
			if (numeric == Integer.parseInt(numericStr)) {
				return TABLE[row][COLUMN_ALPHABETIC];
			}
		}
		return "";
	}

	private Iso4217() {
	}
}
