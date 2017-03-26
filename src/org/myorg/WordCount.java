package org.myorg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class WordCount extends Configured implements Tool {

	private static final Logger LOG = Logger.getLogger(WordCount.class);
	private static NumberFormat nf = new DecimalFormat("00");

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new WordCount(), args);

		System.exit(res);
	}

	public int run(String[] args) throws Exception {
		// convertInputfile("Input/p2p-Gnutella31.txt");

		boolean isCompleted = convertInputfile("Input/p2p-Gnutella31.txt",
				"output/PageRank/iter00");
		if (!isCompleted)
			return 1;

		String lastResultPath = null;

		for (int runs = 0; runs < 10; runs++) {

			String inPath = "output/PageRank/iter" + nf.format(runs);
			lastResultPath = "output/PageRank/iter" + nf.format(runs + 1);

			isCompleted = pageRankMethod(inPath, lastResultPath);

		}

		return isCompleted ? 0 : 1;

	}

	public Boolean convertInputfile(String inputPath, String outputPath)
			throws IOException, ClassNotFoundException, InterruptedException {

		Job job = Job.getInstance(getConf(), "Order");
		job.setJarByClass(this.getClass());
		// Use TextInputFormat, the default unless job.setInputFormatClass is
		// used

		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.setMapperClass(OrderMap.class);
		job.setReducerClass(OrderReduce.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		return job.waitForCompletion(true);
	}

	public boolean pageRankMethod(String inPath, String lastResultPath)
			throws IOException, ClassNotFoundException, InterruptedException {

		Job job = Job.getInstance(getConf(), "PageRank");
		job.setJarByClass(this.getClass());
		// Use TextInputFormat, the default unless job.setInputFormatClass is
		// used

		FileInputFormat.addInputPath(job, new Path(inPath));
		FileOutputFormat.setOutputPath(job, new Path(lastResultPath));
		job.setMapperClass(Map.class);

		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		return job.waitForCompletion(true);

	}

	public static class Map extends
			Mapper<LongWritable, Text, IntWritable, Text> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			Node fromNode = new Node("fromNode,"
					+ value.toString().replaceAll("\\s", ""));

			double nodeRank = fromNode.getNodeRank()
					/ fromNode.getAdjacencyList().size();

			context.write(new IntWritable(fromNode.getNodeId()), new Text(
					fromNode.toString()));

			if (!fromNode.getAdjacencyList().isEmpty()) {

				for (Integer node : fromNode.getAdjacencyList()) {
					Node toNode = new Node("toNode," + nodeRank);

					context.write(new IntWritable(node),
							new Text(toNode.toString()));
				}
			}

		}

	}

	public static class Reduce extends
			Reducer<IntWritable, Text, IntWritable, Text> {

		@Override
		public void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {

			double pagerank = 0.0;
			Node fromNode = null;

			for (Text value : values) {

				Node inNode = new Node(value.toString());
				if (inNode.isNode()) {
					fromNode = inNode;
				} else {

					pagerank += inNode.getNodeRank();
				}
			}

			pagerank = 1 - 0.85 + (0.85 * pagerank);
			String adjacency = "";
			System.out.println("KO");
			if (!fromNode.getAdjacencyList().isEmpty()) {
				for (int i = 0; i < fromNode.getAdjacencyList().size(); i++) {
					adjacency += "," + fromNode.getAdjacencyList().get(i);
				}
			}
			System.out.println(fromNode.getNodeId()+ ","
					+ pagerank + adjacency);
			context.write(new IntWritable(fromNode.getNodeId()), new Text(","
					+ pagerank + adjacency));

		}
	}

	public static class OrderMap extends
			Mapper<LongWritable, Text, IntWritable, Text> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			;
			String[] splited = value.toString().split("\\s+");

			int nodeFrom = Integer.parseInt(splited[0]);
			int nodeTo = Integer.parseInt(splited[1]);

			context.write(new IntWritable(nodeFrom), new Text("" + nodeTo));
			context.write(new IntWritable(nodeTo), new Text(""));
		}
	}

	public static class OrderReduce extends
			Reducer<IntWritable, Text, IntWritable, Text> {

		@Override
		public void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			String adjacencyList = "";
			for (Text value : values) {
				if (!value.toString().isEmpty()) {
					adjacencyList += "," + value;
				}
			}

			context.write(key, new Text(",1" + adjacencyList));

		}
	}
}
