# BimEdit

## Introduction
Course work for Lausekielinen Ohjelmointi 2 course

Written in java, with test scrips in bash and batch

## Running
After compiling, BimEdit takes two arguments:
* the name of a text file with the source image in PPM-like format
* the optional parameter "echo".

The file will be loaded, and the program will quit if the file is malformed or doesn't exist. The echo parameter prints out all commands that the program gets as input and is useful for debugging.

## Image format
The first two rows of the file are integers that contain the dimensions of the image file, with the first one being height and the second one being width. Rows three and four are the "back symbol" and "front symbol" respectively. If the image doesn't match the dimensions or contains symbols not included within the symbol fields, the image is considered malformed and the program wil exit.

