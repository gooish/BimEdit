#!/bin/bash
java BimEdit image_x.txt < input_brain_check1.txt > out1.txt &

diff -y out1.txt output_brain_check1.txt &

java BimEdit image_plus.txt < input_brain_check2.txt > out2.txt &

diff -y out2.txt output_brain_check2.txt &

java BimEdit image_small_rectangle.txt < input_dilate_rectangle.txt > out3.txt &

diff -y out3.txt output_dilate_rectangle.txt &

java BimEdit image_large_rectangle.txt < input_erode_rectangle.txt > out4.txt &

diff -y out4.txt output_erode_rectangle.txt &

java BimEdit image_spot.txt < input_spot.txt > out5.txt &

diff -y out5.txt output_spot.txt &

java BimEdit image_spots.txt < input_spots.txt > out6.txt &

diff -y out6.txt output_spots.txt &

java BimEdit image_cat.txt < input_cat.txt > out7.txt &

diff -y out7.txt output_cat.txt &

java BimEdit image_spot.txt < input_errors.txt > out8.txt &

diff -y out8.txt output_errors.txt &

java BimEdit > out9.txt &

diff -y out9.txt output_invalid_args1.txt &

java BimEdit spot.txt b c > out10.txt &

diff -y out10.txt output_invalid_args2.txt &

java BimEdit spot.txt abc > out11.txt &

diff -y out11.txt output_invalid_args3.txt &

java BimEdit x-file.txt > out12.txt &

diff -y out12.txt output_x-file.txt &

java BimEdit image_too_colourful.txt > out13.txt &

diff -y out13.txt output_too_colourful.txt &

java BimEdit image_too_short.txt > out14.txt &

diff -y out14.txt output_too_short.txt &

java BimEdit image_too_tall.txt > out15.txt &

diff -y out15.txt output_too_tall.txt &

java BimEdit image_too_thin.txt > out16.txt &

diff -y out16.txt output_too_thin.txt &

java BimEdit image_too_wide.txt > out17.txt &

diff -y out17.txt output_too_wide.txt &

java BimEdit image_too_twisted.txt > out18.txt &

diff -y out18.txt output_too_twisted.txt 
