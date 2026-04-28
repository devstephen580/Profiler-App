package com.devStephen.profiler_api.util;

import java.util.HashMap;
import java.util.Map;

public class CountryMapper {

  public static final Map<String, String> COUNTRY_NAMES  = new HashMap<>();

  static {
    COUNTRY_NAMES .put("nigeria", "NG");
    COUNTRY_NAMES .put("ghana", "GH");
    COUNTRY_NAMES .put("kenya", "KE");
    COUNTRY_NAMES .put("tanzania", "TZ");
    COUNTRY_NAMES .put("uganda", "UG");
    COUNTRY_NAMES .put("ethiopia", "ET");
    COUNTRY_NAMES .put("egypt", "EG");
    COUNTRY_NAMES .put("south africa", "ZA");
    COUNTRY_NAMES .put("cameroon", "CM");
    COUNTRY_NAMES .put("senegal", "SN");
    COUNTRY_NAMES .put("angola", "AO");
    COUNTRY_NAMES .put("mozambique", "MZ");
    COUNTRY_NAMES .put("madagascar", "MG");
    COUNTRY_NAMES .put("sudan", "SD");
    COUNTRY_NAMES .put("niger", "NE");
    COUNTRY_NAMES .put("mali", "ML");
    COUNTRY_NAMES .put("burkina faso", "BF");
    COUNTRY_NAMES .put("malawi", "MW");
    COUNTRY_NAMES .put("zambia", "ZM");
    COUNTRY_NAMES .put("zimbabwe", "ZW");
    COUNTRY_NAMES .put("rwanda", "RW");
    COUNTRY_NAMES .put("benin", "BJ");
    COUNTRY_NAMES .put("togo", "TG");
    COUNTRY_NAMES .put("sierra leone", "SL");
    COUNTRY_NAMES .put("libya", "LY");
    COUNTRY_NAMES .put("tunisia", "TN");
    COUNTRY_NAMES .put("algeria", "DZ");
    COUNTRY_NAMES .put("morocco", "MA");
    COUNTRY_NAMES .put("somalia", "SO");
    COUNTRY_NAMES .put("united states", "US");
    COUNTRY_NAMES .put("usa", "US");
    COUNTRY_NAMES .put("united kingdom", "GB");
    COUNTRY_NAMES .put("uk", "GB");
    COUNTRY_NAMES .put("india", "IN");
    COUNTRY_NAMES .put("france", "FR");
    COUNTRY_NAMES .put("germany", "DE");
    COUNTRY_NAMES .put("brazil", "BR");
    COUNTRY_NAMES .put("canada", "CA");
    COUNTRY_NAMES .put("australia", "AU");
    COUNTRY_NAMES .put("china", "CN");
    COUNTRY_NAMES .put("japan", "JP");
    COUNTRY_NAMES .put("pakistan", "PK");
    COUNTRY_NAMES .put("indonesia", "ID");
    COUNTRY_NAMES .put("mexico", "MX");
    COUNTRY_NAMES .put("philippines", "PH");
    COUNTRY_NAMES .put("congo", "CG");
    COUNTRY_NAMES .put("democratic republic of congo", "CD");
    COUNTRY_NAMES .put("ivory coast", "CI");
    COUNTRY_NAMES .put("cote d'ivoire", "CI");
  }

  public static String getCountryCode(String countryName) {
    if (countryName == null) return null;
    return COUNTRY_NAMES .get(countryName.trim().toLowerCase());
  }
}