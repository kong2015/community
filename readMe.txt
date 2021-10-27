如果Kafka启动报错 删除0000logs下对应的kafka日志文件夹，重新启动zookeeper和kafka
启动kafka：
			第一步、先启动zookeeper：
			d:
			cd d:\Java\kafka_2.12-2.3.0
			bin\windows\zookeeper-server-start.bat config\zookeeper.properties
			
			第二步、启动kafka：
			d:
			cd d:\Java\kafka_2.12-2.3.0
			bin\windows\kafka-server-start.bat config\server.properties


启动elasticsearch：双击D:\Java\elasticsearch-7.12.1\bin\下面的elasticsearch.bat