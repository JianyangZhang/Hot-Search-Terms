package com.textprediction.ngramlm;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class NGramBuilder {
	public static class NGramMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		private static int GRAM_NUMBER; // the number n of n-gram
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			GRAM_NUMBER = conf.getInt("GRAM_NUMBER", 5);		
		}
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// get a collection of "clean words" from a complete sentence
			String line = value.toString();
			line = line.trim().toLowerCase();
			line = line.replaceAll("[^a-z]", " ");
			String[] words = line.split("\\s+");
			
			// generate n-gram entries from this sentence
			StringBuilder sb;
			if (words.length < 2) {
				return;
			}			
			for (int i = 0; i < (words.length - 1); i++) {
				sb = new StringBuilder();
				sb.append(words[i]);
				for (int j = 1; (i + j) < words.length && j < GRAM_NUMBER; j++) {
					sb.append(" ");					
					sb.append(words[i + j]);
					context.write(new Text(sb.toString().trim()), new IntWritable(1));
				}
			}
		}
	}
	
	public static class NGramReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			// build n-gram library
			int sum = 0;
			for (IntWritable value : values) {
				sum = sum + value.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
}
