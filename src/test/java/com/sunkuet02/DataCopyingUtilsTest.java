package com.sunkuet02;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by sun on 4/4/17.
 */
public class DataCopyingUtilsTest {

    @Test
    public void testCapitalize() {
        System.out.println("================ Testing capitalize=================");
        String testString = "abcd";
        String stringShouldReturn = "Abcd";
        String convertedString = DataCopyingUtils.capitalize(testString);

        Assert.assertEquals(stringShouldReturn, convertedString);
    }

    @Test
    public void testCopyDataToObject() {
        Animal1 animal1 = new Animal1();
        Animal2 animal2 = new Animal2();

        System.out.println("\n\n==============Testing CopyDataToObject================= * 1");
        animal1.setName("animal");
        animal1.setType("an");
        try {
            animal2 = (Animal2) DataCopyingUtils.copyDataToObject(animal1, animal2);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(animal2.getAnimalName(), "animal");
        Assert.assertEquals(animal2.getType(), "an");

        System.out.println("\n==============Testing CopyDataToObject================= * 2");

        Address address = new Address("Rangpur", "Gangachara");
        animal1.setAddress(address);

        try {
            animal2 = (Animal2) DataCopyingUtils.copyDataToObject(animal1, animal2);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(animal2.getAddress().getDistrict(), "Rangpur");
        Assert.assertEquals(animal2.getAddress().getThana(), "Gangachara");
    }


    private class Animal1 {
        String name;
        String type;

        Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private class Animal2 {
        @CopyObjectData(name = "animalName", replacedBy = "name")
        String animalName;
        String type;

        Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getAnimalName() {
            return animalName;
        }

        public void setAnimalName(String animalName) {
            this.animalName = animalName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private class Address{
        private String district;
        private String thana;

        public Address(String district, String thana) {
            this.district = district;
            this.thana = thana;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getThana() {
            return thana;
        }

        public void setThana(String thana) {
            this.thana = thana;
        }
    }
}
