package com.textprediction.ngramlm;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

// formatted output to mySQL database: starter follower probability
public class DBOutputWritable implements Writable, DBWritable {
	private String starter;
	private String follower;
	private int probability;
	
	public DBOutputWritable(String starter, String follower, int probability) {
		this.starter = starter;
		this.follower = follower;
		this.probability = probability;
	}

	@Override
	public void readFields(ResultSet arg0) throws SQLException {
		this.starter = arg0.getString(1);
		this.follower = arg0.getString(2);
		this.probability = arg0.getInt(3);
	}

	@Override
	public void write(PreparedStatement arg0) throws SQLException {
		arg0.setString(1, starter);
		arg0.setString(2, follower);
		arg0.setInt(3, probability);
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
	}	
}
