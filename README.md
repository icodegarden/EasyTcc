# EasyTcc
High performance and easy-to-use TCC distributed transaction framework based on spring, real-time (optional), weak consistency, compensation recovery,etc..基于spring的高性能、易用的TCC分布式事务框架，实时（可选），弱一致、补偿恢复等 

# [Quick Start](https://github.com/HiFangfangXu/EasyTcc/wiki/Quick-Start)

一、原理概念
	
	Try: 尝试执行业务

	Confirm: 确认执行业务

		如有非本地事务支持的操作，需满足幂等性
	Cancel: 取消执行业务

		如有非本地事务支持的操作，需满足幂等性

二、使用说明

	##spring配置 扫描 cn.evun.sweet.dtx
	
	##编码中使用注解TwoPhaseService
		action为该业务描述，在根请求中设置即可，也可不设置，用于后期有控制台功能时展示
		confirmMethod 对应的confirm方法的名称，必须
		cancelMethod 对应的cancel方法的名称，必须
		applications 被调用服务的名称，服务名称设置在 dtx.properties 的 application.name
		如转出服务调用了转入服务和积分服务，设置为applications={"bankin","score"}，最底层服务没有调用其他服务不需要设置
	
		@TwoPhaseService(action="转账",confirmMethod="confirm",cancelMethod="cancel",applications={"bankin","score"})
		@Transactional
		public TransferInOrder transferAmt(TransferInDto transferInDto) {
			执行本地操作
			调用转入服务
			调用积分服务
		}
		
	##classpath下增加 dtx.properties
	 	配置说明：
	 	application.name	本应用系统名，必须，同一集群保持一致，与使用中的注解 @TwoPhaseService applications={"score"} 相对应

		signal.channel = rabbitmq	使用rabbitmq作为中间件，暂不支持其他类型
		signal.rabbitmq.host = 10.200.154.151
		signal.rabbitmq.username = root
		signal.rabbitmq.password = 123456
		
		使用jedisPool或JedisSentinelPool，任选一项
		jedisPool.host = 10.200.154.151
		jedisPool.port = 6379
		jedisPool.connectionTimeout = 3000
		jedisPool.soTimeout = 3000
		jedisPool.password
		jedisPool.database
		jedisPool.maxTotal = 10
		jedisPool.maxIdle= 10
		jedisPool.minIdle= 10
		
		使用jedisPool或JedisSentinelPool，任选一项
		JedisSentinelPool.masterName
		JedisSentinelPool.sentinels
		JedisSentinelPool.connectionTimeout
		JedisSentinelPool.soTimeout
		JedisSentinelPool.password
		JedisSentinelPool.database
		JedisSentinelPool.maxTotal
		JedisSentinelPool.maxIdle
		JedisSentinelPool.minIdle
		
		#default true	是否开启失败事务的定时恢复
		recovery.open = true
		#default 0/30 * * * * ?	事务定时检查恢复频率
		recovery.cronExpression = 0/30 * * * * ?
		#default 5	每个失败的事务恢复最大尝试次数
		recovery.maxRetryTimes = 5
		
		#default true	是否开启度量
		metrics.open = true

	##请求参数要实现 Serializable

三、示例demo
	
	见完整demo项目
