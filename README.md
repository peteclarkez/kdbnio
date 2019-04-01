### KDB Java Interface using NIO

- Looks like the NanoClockSource isn't a high enough resolution on Windows for good results
- Next Task will be to merge the PasswordHandler

Using Java Sockets
```
QBulkTest Total Time = 1,761,004,831ns
QBulkTest Avg Time = 17us
QBulkTest Rate = 58,823 msg/sec
QBulkTest Got Dictionary [count, min, avg, max, 50, 90, 99, 99.9, 99.99]
QBulkTest Got Dictionary [109,331; 499; 64,359; 173,962; 52,145; 108,789; 143,883; 153,910; 173,462]
```

Using Netty NIO 

```
QNettyBulkTest Total Time = 159,769,552ns
QNettyBulkTest Avg Time = 1us
QNettyBulkTest Rate = 1,000,000 msg/sec
QNettyBulkTest Got Dictionary [count, min, avg, max, 50, 90, 99, 99.9, 99.99]
QNettyBulkTest Got Dictionary [110,000; 501; 1,391,410; 2,120,928; 1,598,750; 2,001,872; 2,109,332; 2,119,926; 2,120,928]
```
