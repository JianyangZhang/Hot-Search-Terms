package com.textprediction.ngramlm;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

@SuppressWarnings("deprecation")
public class Dispatcher {
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
			// configure n-gram mapreduce job
			Configuration conf1 = new Configuration();
			conf1.set("textinputformat.record.delimiter", "."); // read a complete sentence as a line
			conf1.set("GRAM_NUMBER", args[2]);
			Job job1 = Job.getInstance(conf1);
			job1.setNumReduceTasks(3);
			job1.setJobName("NGram");
			job1.setJarByClass(Dispatcher.class);
			job1.setMapperClass(NGramBuilder.NGramMapper.class);
			job1.setReducerClass(NGramBuilder.NGramReducer.class);
			job1.setOutputKeyClass(Text.class);
			job1.setOutputValueClass(IntWritable.class);
			job1.setInputFormatClass(TextInputFormat.class); // default format: reads lines of text files
			job1.setOutputFormatClass(TextOutputFormat.class); // default format: key \t value
			TextInputFormat.setInputPaths(job1, new Path(args[0]));
			TextOutputFormat.setOutputPath(job1, new Path(args[1]));
			job1.waitForCompletion(true); // language model won't start to build until the n-gram library completely built
			
			// configure language model mapreduce job
			Configuration conf2 = new Configuration();
			conf2.set("THRESHOLD", args[3]);
			conf2.set("TOP_K", args[4]);
			DBConfiguration.configureDB(conf2, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/tp", "root", "123456"); // establish connection with mySQL database   
			Job job2 = Job.getInstance(conf2);
			job2.setNumReduceTasks(3);
			job2.setJobName("LModel");
			job2.setJarByClass(Dispatcher.class);			
			job2.addArchiveToClassPath(new Path("/mysql/mysql-connector-java-5.1.39-bin.jar")); // putting this jar file into jre/lib/ext is recommended	
			job2.setMapperClass(LanguageModel.ModelMapper.class);
			job2.setReducerClass(LanguageModel.ModelReducer.class);
			job2.setMapOutputKeyClass(Text.class); // Mapper emits different key type than the Reducer
			job2.setMapOutputValueClass(Text.class); // Mapper emits different value type than the Reducer
			job2.setOutputKeyClass(DBOutputWritable.class);
			job2.setOutputValueClass(NullWritable.class);
			job2.setInputFormatClass(TextInputFormat.class);
			job2.setOutputFormatClass(DBOutputFormat.class);
			TextInputFormat.setInputPaths(job2, new Path(args[1]));
			DBOutputFormat.setOutput(job2, "LanguageModel", new String[] {"starter", "follower", "probability"});
			System.exit(job2.waitForCompletion(true) ? 0 : 1);
	}
}