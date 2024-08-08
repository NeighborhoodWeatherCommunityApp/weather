package org.pknu.weather.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CoordinateConversionUtilsTest {

    @Test
    void 위경도를_격자로_변환하는_테스트() {
        Integer x = 53;
        Integer y = 33;
        Float lon = 126.552733333333f;
        Float lat = 33.2606333333333f;

        // given
        Point point = CoordinateConversionUtils.convertCoordinate(lon, lat);

        System.out.println(point.x + " " + point.y + " " + lon + " " + lat);

        // then
        Assertions.assertThat(point.x).isEqualTo(x);
        Assertions.assertThat(point.y).isEqualTo(y);
    }
}
