**Search Engine Autocompletion:**<br/>
![show](https://cloud.githubusercontent.com/assets/22739177/21748185/f34673c6-d532-11e6-9b4c-e6cacec76eea.PNG)<br/>
*The following words can be predicted along with the user's typing.*<br/>
*The predictions are based on the probabilities in natural language that specific words usually appear after a given word or phrase.*<br/>

This project firstly generates an n-gram library from training data, then builds a language model based on the generated n-gram library
and stores the model to mySQL database . This language model can be used to predict the words after a given word or phrase.

**Sample mySQL database setup:<br/>**
mysql> create database tp;<br/>
mysql> use tp;<br/>
mysql> create table LanguageModel(starter varchar(250), follower varchar(250), probability int);<br/>
mysql> grant all on \*.\* to "root"@"%" identified by "123456";<br/>
mysql> flush privileges;<br/>
![tp1](https://cloud.githubusercontent.com/assets/22739177/21748178/f31d7eda-d532-11e6-8990-3459fb19bfe3.PNG)<br/>

**Preparation in HDFS:**<br/>
hdfs dfs -mkdir /mysql<br/>
hdfs dfs -put ./mysql-connector-java-5.1.39-bin.jar /mysql<br/>
hdfs dfs -mkdir /trainingdata<br/>
hdfs dfs -put ./trainingdata/* /trainingdata<br/>
![tp2](https://cloud.githubusercontent.com/assets/22739177/21748179/f334987c-d532-11e6-8e1f-00c01b09796f.PNG)

**Run the project:**<br/>
Usage: hadoop jar \<jar file\> \<main class name\> \<input dir\> \<output dir\> [GRAM_NUMBER] [THRESHOLD] [TOP_K]<br/>
hadoop jar TextPrediction.jar com.textprediction.ngramlm.Dispatcher /trainingdata /ngram 5 5 5<br/>
GRAM_NUMBER: the number n of the n-gram; default 5;<br/>
THRESHOLD: threshold for a phrase to be semantic; default 5;<br/>
TOP_K: only show the top k predictions based on their probabilities; default 5;<br/>

**Check the first mapreduce job results that generate an n-gram library:**<br/>
hdfs dfs -ls /ngram<br/>
hdfs dfs -get /ngram/part-r-* ./generated-n-gram-library/<br/>
![tp3](https://cloud.githubusercontent.com/assets/22739177/21748181/f339a3f8-d532-11e6-89f2-1983d254da05.PNG)<br/>

**The second mapreduce job produces a language model looks like this in mySQL database:**<br/>
select \* from LanguageModel limit 50;<br/>
select \* from LanguageModel into outfile '/tmp/generated-language-model/LanguageModel.out';<br/>
![tp4](https://cloud.githubusercontent.com/assets/22739177/21748180/f339a556-d532-11e6-9968-aa9cc21e48a0.PNG)<br/>

**Sample predictions:**<br/>
mySQL> select * from LanguageModel where starter like 'input%' order by probability desc limit x;<br/>
&nbsp;&nbsp;user input "this is", predictions are<br/>
![tp5](https://cloud.githubusercontent.com/assets/22739177/21748184/f33c40b8-d532-11e6-8b5c-71003ea384f5.PNG)<br/>
&nbsp;&nbsp;user input "away from", predictions are<br/>
![tp6](https://cloud.githubusercontent.com/assets/22739177/21748182/f33a017c-d532-11e6-85f2-0d791087da9b.PNG)<br/>
&nbsp;&nbsp;user input "you", predictions are<br/>
![tp7](https://cloud.githubusercontent.com/assets/22739177/21748183/f33a62fc-d532-11e6-9b11-7d0230d5ff8d.PNG)<br/>


