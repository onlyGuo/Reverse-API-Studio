package com.guoshengkai.reverseapistudio.utils.script;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Cloudflare {

    public static String fuck(String url){
        WebDriver driver = new ChromeDriver();
        driver.navigate().to(url);
        System.out.println("111");
        driver.navigate().to("https://lmarena.ai/?mode=direct");
        return null;
    }

    public static void main(String[] args) {
        System.out.println(fuck("https://lmarena.ai/"));
    }

}
