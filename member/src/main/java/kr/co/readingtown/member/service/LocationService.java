package kr.co.readingtown.member.service;


import java.math.BigDecimal;

public interface LocationService {

    String resolveTown(BigDecimal longitude, BigDecimal latitude);
}


