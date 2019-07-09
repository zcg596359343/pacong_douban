package com.cn;

import com.alibaba.fastjson.JSON;
import com.cn.mapper.MovieMapper;
import com.cn.model.Movie;
import com.cn.util.GetJson;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

    class Main {
    public static  void  main(String [] args) {

        String resource = "mybatis-config.xml"; //定义配置文件路径
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);//读取配置文件
        } catch (IOException e) {
            e.printStackTrace();
        }

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//注册mybatis 工厂

        SqlSession sqlSession = sqlSessionFactory.openSession();//得到连接对象

        MovieMapper movieMapper = sqlSession.getMapper(MovieMapper.class);//从mybatis中得到dao对象

        int start;//每页多少条
        int total = 0;//记录数
        int end = 9979;//总共9979条数据
        for (start  = 0; start <= end; start += 20)  {
            try {

                String address = "https://Movie.douban.com/j/new_search_subjects?sort=U&range=0,10&tags=&start=" + start;

                JSONObject dayLine = new GetJson().getHttpJson(address, 1);

                System.out.println("start:" + start);
                JSONArray json = dayLine.getJSONArray("data");
                List<Movie> list = JSON.parseArray(json.toString(), Movie.class);

                if (start <= end){
                    System.out.println("已经爬取到底了");

                }
                for (Movie movie : list) {
                    movieMapper.insert(movie);
                    sqlSession.commit();
                }
                total += list.size();
                System.out.println("正在爬取中---共抓取:" + total + "条数据");

            } catch (Exception e) {
                e.printStackTrace();
                sqlSession.close();
            }

        }
    }

}
