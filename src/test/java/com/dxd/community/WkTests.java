package com.dxd.community;

import java.io.IOException;

/**
 * @author dxd
 * @create 2021-07-19 15:52
 */
public class WkTests {
    public static void main(String[] args) {
        String cmd =
                "D:\\Java\\wkhtmltopdf\\bin\\wkhtmltoimage --quality 75 https://baidu.com D:/Javaspace/0000wkhtmltopdf/wk-pdfs/1.pdf";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ok");
    }
}
