---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by hacker.
--- DateTime: 2024/6/14 15:45
---
local vouchId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

local stockKey = 'seckill:stock:' .. vouchId
local orderKey = 'seckill:order:' .. vouchId

if (tonumber(redis.call('get', stockKey)) <= 0) then
    return 1
end

if (redis.call('sismember', orderKey, userId) == 1) then
    return 2
end

redis.call('incrby', stockKey, -1)
redis.call('sadd', orderKey, userId)
redis.call('xadd', 'stream.orders', '*', 'vouchId', vouchId, 'userId', userId, 'id', orderId)

return 0