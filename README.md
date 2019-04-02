# EasyTcc
High performance and easy-to-use TCC distributed transaction framework based on spring, real-time (optional), weak consistency, compensation recovery,etc..基于spring的高性能、易用的TCC分布式事务框架，实时（可选），弱一致、补偿恢复等 

# Concepts 概念
Try: Try to execute business  
尝试执行业务  
Confirm: Identity is required to confirm the execution of business  
确认执行业务，需满足幂等性  
Cancel: To cancel the execution of business, idempotency must be satisfied  
取消执行业务，需满足幂等性

# [Quick Start](https://github.com/HiFangfangXu/EasyTcc/wiki/Quick-Start)

# How it works
## xid and xidCreateStack
xid is Global transaction ID,xidCreateStack is a stack for creating global transaction ID  
xid是一个全局事务ID，xidCreateStack是创建全局事务的方法栈  
by default, transactions will be processed real time by netty(Contains local and remote transactions),which is started in xidCreateStack  
默认情况事务将使用netty被实时处理(包含本地事务和远程事务),这项工作从 xidCreateStack 发起
if a transaction fails (network reasons, etc.), it will be reserved for recovery until the number of failed configurations  
事务若处理失败（网络原因等），将被保留等待恢复，直到配置的失败次数

## localTransaction and localTransactionBeginStack
local transaction is the local transaction of each service in this global transaction,Because a local transaction may have multiple transaction annotations, the local Transaction BeginStack is the method stack for creating local transactions  
local transaction 是每个服务在本次全局事务中的本地事务,由于一个本地事务可能有多处使用事务注解，localTransactionBeginStack 是本地事务的创建方法栈  
Local Transaction BeginStack saves local transaction data after tcc's try phase, which is used for comfirm/cancel decision-making in the next phase.  
localTransactionBeginStack 会在tcc的try阶段结束后保存本地事务数据，用于下阶段的comfirm/cancel作出决策  

## transaction recovery
Transactions that fail to commit, roll back (real-time failures, eventual consensus failures, etc.) are retried automatically until the number of failures reaches the number of configurations, as detailed in the [configuration](https://github.com/HiFangfangXu/EasyTcc/wiki/Configuration).  
After reaching the number of times, the global transaction and local transaction data will be transferred to the failure area for queries to be processed manually.  
自动会对提交、回滚失败（实时失败、最终一致失败等）的事务进行重试，直到失败次数达到所配置的次数，详见[配置](https://github.com/HiFangfangXu/EasyTcc/wiki/Configuration)
达到次数后，全局事务、本地事务数据将被移交至失败区，可供查询人工处理  
