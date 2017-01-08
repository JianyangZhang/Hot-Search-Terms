package com.textprediction.ngramlm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class LanguageModel {
	public static class ModelMapper extends Mapper<LongWritable, Text, Text, Text> {
		private static int THRESHOLD; // threshold for this phrase to be semantic  
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			THRESHOLD = conf.getInt("THRESHOLD", 5);
		}
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// convert an n-gram entry to a key-value pair in the language model for text prediction
			// ex: convert "you are my sunshine\t100" to "you are my"=>"sunshine=100"
			// it means that "sunshine" following "you are my" appeared 100 times 
			if (value == null || value.toString().trim().length() == 0) {
				return;
			}
			String line = value.toString().trim();
			String[] chunks = line.split("\t");
			if (chunks.length < 2) {
				return;
			}
			String[] words = chunks[0].split("\\s+");
			int sum = Integer.parseInt(chunks[1]);
			if (sum < THRESHOLD) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < words.length - 1; i++) {
				sb.append(words[i]);
				sb.append(" ");
			}
			String outputKey = sb.toString().trim();
			String outputValue = words[words.length - 1] + "=" + sum;
			if (outputKey != null && outputKey.length() >= 1) {
				context.write(new Text(outputKey), new Text(outputValue));
			}
		}		
	}
	public static class ModelReducer extends Reducer<Text, Text, DBOutputWritable, NullWritable> {
		private static int TOP_K; // only show the top k predictions based on their probabilities
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			TOP_K = conf.getInt("TOP_K", 5);
		}
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			// convert: "you are my"=>["destiny=80", "everything=80", "sunshine=100"]
			// to: [100=>["sunshine"], 80=>["destiny", "everything"]]
			TreeMap<Integer, List<String>> map = new TreeMap<>(Collections.reverseOrder());
			for (Text value : values) {
				String chunk = value.toString().trim();
				String word = chunk.split("=")[0].trim();
				int sum = Integer.parseInt(chunk.split("=")[1].trim());
				if (map.containsKey(sum)) {
					map.get(sum).add(word);
				} else {
					List<String> list = new ArrayList<>();
					list.add(word);
					map.put(sum, list);
				}
			}
			Iterator<Integer> iter = map.keySet().iterator();
			for (int i = 0; iter.hasNext() && i < TOP_K; i++) {
				int sum = iter.next();
				List<String> words = map.get(sum);
				for (String word : words) {
					// "key.toString()" is the "starter" in database
					// "word" is the "follower" in database
					// "sum" is the "probability" in database
					context.write(new DBOutputWritable(key.toString(), word, sum), NullWritable.get());
					i++;
				}
			}
		}
	}	
}
