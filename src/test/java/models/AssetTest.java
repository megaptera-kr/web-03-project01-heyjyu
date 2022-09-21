package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    void creation() {
        String name = "삼성전자";
        Double averagePrice = 56000.0;
        Double count = 10.0;
        Double currentUnitPrice = 55000.0;

        Asset asset1 = new Asset(name, averagePrice, count, currentUnitPrice);
        Asset asset2 = new Asset(name, averagePrice, count, currentUnitPrice);

        assertNotEquals(asset1.id(), asset2.id());
        assertEquals("삼성전자", asset1.name());
        assertEquals(56000.0, asset1.averagePrice());
        assertEquals(10.0, asset1.count());
        assertEquals(55000.0, asset1.currentUnitPrice());
    }

    @Test
    void process() {
        String name = "삼성전자";
        Double averagePrice = 56000.0;
        Double count = 10.0;
        Double currentUnitPrice = 55000.0;

        Asset asset = new Asset(name, averagePrice, count, currentUnitPrice);

        String type = "매수";
        averagePrice = 54000.0;
        count = 10.0;
        currentUnitPrice = 56000.0;

        asset.process(type, averagePrice, count, currentUnitPrice);

        assertEquals(55000.0, asset.averagePrice());
        assertEquals(20.0, asset.count());
        assertEquals(56000.0, currentUnitPrice);

        asset.process("매도", averagePrice, count, averagePrice);

        assertEquals(56000.0, asset.averagePrice());
        assertEquals(10.0, asset.count());
        assertEquals(56000.0, currentUnitPrice);
    }

    @Test
    void valuation() {
        String name = "삼성전자";
        Double averagePrice = 56000.0;
        Double count = 10.0;
        Double currentUnitPrice = 55000.0;

        Asset asset = new Asset(name, averagePrice, count, currentUnitPrice);

        assertEquals(550000.0, asset.valuation());
    }

    @Test
    void totalPurchase() {
        String name = "삼성전자";
        Double averagePrice = 56000.0;
        Double count = 10.0;
        Double currentUnitPrice = 55000.0;

        Asset asset = new Asset(name, averagePrice, count, currentUnitPrice);

        assertEquals(56000.0 * 10.0, asset.totalPurchase());
    }
}
