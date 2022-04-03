# le Explo Only
```mermaid
graph
Explo ---> Send_Ping
Send_Ping ---> Check_Ping

Check_Ping --Ping--> Send_ACK_Entire_Map
Check_Ping --No_Ping--> Check_ACK

Check_ACK --ACK--> Send_Partial_Map
Send_Partial_Map ---> Check_Ping
Check_ACK --No_ACK & No_Timeout--> Check_Ping
Check_ACK --No_ACK & Timeout--> Explo

Send_ACK_Entire_Map ---> Wait+Check_Map
Wait+Check_Map ---> Check_Ping
```

# Explo & Collect
```mermaid
graph

Move ---> Send_Ping

Send_Ping ---> Check

Check --Ping--> Send_ACK_Entire_Map
Check --ACK--> Send_Partial_Map
Check --Treasure--> Decide
Check --Nothing--> Check
Check --Timeout--> Move

Send_Partial_Map ---> Check

Send_ACK_Entire_Map ---> Wait_Partial_Map
Wait_Partial_Map --Timeout/Received--> Check

Decide ---> Check
Decide --Ratio_OK & Bag_OK & Role_OK & Mode_OK--> Collect

Collect ---> Send_Ping
```

## Move: 3 modes: 
### Explo
Explore / complete map
### Locate
Get all the known treasures
### Search
Look for unknown treasures

## Check: Manage order / priority
### Treasure
Pass to Treasure only one time

## Decide: 4 factors
### Bag
If bag is empty
### Mode
Maybe not a factor?
### Role 
If the role correspond
### Ratio
If the ratio permits me to become collector a said type
Global ratio is updated through the quantities of treasures on the map




