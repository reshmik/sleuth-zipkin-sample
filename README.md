# sleuth-zipkin-sample
This is an sample app for testing how spring cloud sleuth stream and zipkin stream works on pcf.

Sleuth Zipkin Stream Set Up for pcf :
1) Deploy zipkin stream on pcf, I used this repo :
https://github.com/spring-cloud/spring-cloud-sleuth/tree/master/spring-cloud-sleuth-samples/spring-cloud-sleuth-sample-zipkin-stream
2) Bind it to rabbit mq, set the RABBIT_HOST with the rabbit mq host just created. 
It should look something like this : 
amqp://0f4c912a-d75c-4be7-b680-57d6814317d4:sss8k3ibcq6vj5ffjaa8a4j75h@10.68.105.88/58ae8d6b-e369-4e45-b1ff-b86d5009a8ee
3) Bind it to mysql, set the MYSQL_HOST with the mysql host just created. 
It should look something like this : 
jdbc:mysql://10.68.105.70:3306/cf_570461ac_d9c8_4322_adc4_d66267f31b63?user=zEqmzJggJRHahleH&password=7hOykC4yNQqMGJFQ

App set up
1) Deploy the sample service application, using manifest.1
2) Bind it to the rabbit mq instance created for zipkin stream above
3) Restart application, the endpoints are : /, /call, /async, /data, /compose, /start
4) You should now see the data on zipkin for service 1
Optional
5) You can now deploy service2 and service3, using manifest.2 and manifest.3
6) Once deployed, if you call /compose endpoint on service3, it should generate a dependency graph on zipkin.
It should look something like this :
![alt tag]()
