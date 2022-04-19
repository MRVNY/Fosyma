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

Check --Ping--> Send_Pong
Check --Pong--> Send_End
Check --Treasure/First_Time_Locate--> Decide
Check --Nothing--> Check
Check --Timeout--> Move

Send_End ---> Check

Send_Pong ---> Wait_End
Wait_End --Timeout/Received--> Check

Decide ---> Check
Decide --Ratio_OK & Bag_OK & Role_OK & Mode_OK--> Collect

Collect ---> Send_Ping
```

# Enchere
```mermaid
graph

Move ---> Send_Ping

Send_Ping ---> Check

Check --Ping--> Send_Pong
Check --Pong--> Send_End
Check --Treasure/First_Time_Locate--> Decide
Check --Nothing/End--> Check
Check --Timeout--> Move

Send_End ---> Check

Send_Pong ---> Check

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

## Decide: decide roles and whether to collect
### Treasure & Value
### Bag
If bag is empty
### Mode
Maybe not a factor?
### Role 
If the role correspond
### Ratio
If the ratio permits me to become collector a said type
Type: 
TotalRatio : Proportion (TRatio)
Average Collected : Equity (ARatio)

~~No role & Locate -> Give role -> Check~~
No role & TRatio_OK & Bag>Value -> Collect
Role_OK & ARatio_OK & Bag>Value -> Collect 
BagFull -> SearchMode -> Check


# Enchere
W > L > E > S
## Explo - Explo / L-L / S-S
avoid going to the same node together

## Other (applicable for Wumpus)
Give space for L by changing the goal node
When colliding, one with less priority switch to next goal, when all goals cause collision, do random move
(extreme case: random)


