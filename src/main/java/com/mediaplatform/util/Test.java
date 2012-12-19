package com.mediaplatform.util;

/**
 * User: timur
 * Date: 12/1/12
 * Time: 3:34 PM
 */
public class Test {
    public static void main(String[] args){
        ImageFormat res = ImageFormat.parse("16x.jpg", FileFormat.JPG);
        System.out.println(res);
    }
}
