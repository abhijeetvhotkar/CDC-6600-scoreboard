Name: Abhijeet Vhotkar (BR62456)
email: abv1@umbc.edu

CMSC 611 – Advanced Computer Architecture

Objective
To experience the design issues of advanced computer architectures through the design of an analyzer for a
simplified MIPS CPU using high level programming languages. The considered MIPS CPU adopts the
CDC 6600 scoreboard scheme to dynamically schedule instruction execution and employ caches in order to
expedite memory access.


The procedure (command) for running on command line is given below:

1.) Extract the zip file to a folder.
2.) For running the program on windows please use the following command lines in cmd.
3.) You can use the makefile for running on linux.

****Command-line statement for EXAMPLE 1********************

java -jar simulator.jar inst.txt data.txt config.txt result.txt


****Command-line statement for EXAMPLE 2*******

java -jar simulator.jar inst_ex_2.txt data.txt config.txt result_ex_2.txt


****Command-line statement for EXAMPLE 3*******

java -jar simulator.jar inst_ex_3.txt data.txt config.txt result_ex_3.txt



The input files are provided as below:

inst.txt

LI R4, 260
LI R5, 272
LI R1, 8
LI R2, 4
LI R3, 0
GG: L.D F1, 4(R4)
L.D F2, 8(R5)
ADD.D F4, F6, F2
SUB.D F5, F7, F1
MUL.D F6, F1, F5
ADD.D F7, F2, F6
ADD.D F6, F1, F7
DADDI R4, R4, 20
DADDI R5, R5, 8
DSUB R1, R1, R2
BNE R1, R3, GG
HLT
HLT

********For EXAMPLE 2*************

inst_ex_2.txt

LI R1, 8
LI R2, 312
LI R4, 256
PP : LW R3, 0(R4)
L.D F1, 12(R4)
ADD.D F3, F1, F1
L.D F2, 28(R4)
ADD.D F1, F2, F3
MUL.D F4, F2, F3
DIV.D F5, F4, F3
MUL.D F6, F1, F3
S.D F6, 32(R2)
ADD.D F4, F1, F3
DADD R4, R1, R2
DADD R3, R4, R3
SW R3, 60(R4)
DSUBI R3, R3, 4
BEQ R2, R3, PP
HLT
HLT

********For EXAMPLE 3*************

LI R1, 16
LI R2, 296
LI R4, 256
QQ: LW R3, 0(R4)
L.D F1, 32(R4)
L.D F2, 64(R4)
ADD.D F4, F1, F2
SUB.D F5, F2, F1
MUL.D F6, F1, F5
DADD R4, R2, R1
DSUBI R1, R1, 8
ADD.D F7, F2, F6
ADD.D F6, F1, F7
SW R4, -24(R2)
S.D F7, 32(R4)
BNE R1, R3, QQ
HLT
HLT


Config.txt
FP adder: 2, 2
FP Multiplier: 2, 30
FP divider: 1, 50
I-Cache: 4, 4

data.txt

00000000000000000000000000000010
00000000000000000010000101010101
00000000001001110101010100010100
00000101010111110001010101010101
00000000101010101010101010101111
00000100010010010101110000001010
00000000000000001111111111111111
00000000000000001111111111111111
00000000000000001111100010100110
00000000000000001010101101110110
00000000000101011010011110101011
00000000000100000000000000000111
00000000000000101110000000010101
00000000000000000010101110101101
00000000000000000000000000000000
00000000000001101101010011110101
00000000000000000000000001100001
00000000000000110000101010101001
00000000000000000000010101010101
00000000000000001111111111111000
00000000000000000000000000000011
00000000000000000000000000000001
00000000000000001111100110100110
00000000000000000001010101010101
00000000000001101101010011110101
00000000000001000010101110010101
00000000000001101101111001010101
00000000000000000101010101110100
00000000000001001010101011110101
00000000000000000000000000010101
00000000000111101110000000000000
00000000000001101101010011110100


Result is stored in result.txt file for Test case 1.
Result is stored in result_ex_2.txt file for Test case 2.
Result is stored in result_ex_3.txt file for Test case 3.


For all the information please open result.txt, result_ex_2.txt, result_ex_3.txt in the folder.
