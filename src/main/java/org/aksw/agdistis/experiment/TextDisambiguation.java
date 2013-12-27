package org.aksw.agdistis.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.aksw.agdistis.algorithm.DisambiguationAlgorithm;
import org.aksw.agdistis.algorithm.NEDAlgo_HITS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datatypeshelper.io.xml.CorpusXmlReader;
import datatypeshelper.utils.corpus.Corpus;

public class TextDisambiguation {
	private static Logger log = LoggerFactory.getLogger(TextDisambiguation.class);

	public static void main(String[] args) throws IOException {
		String languageTag = "en"; // de
		File dataDirectory = new File("/data/r.usbeck/index"); // "/Users/ricardousbeck";
		String nodeType = "http://dbpedia.org/resource/";// "http://yago-knowledge.org/resource/"
		String edgeType = "http://dbpedia.org/ontology/";// "http://yago-knowledge.org/resource/"

		for (String TestFile : new String[] {"datasets/AIDACorpus.xml" }) {
			//"datasets/AIDACorpus.xml" 
			// "german_corpus_new.xml"reuters
			// "datasets/test.xml", "datasets/reuters.xml",
			// "datasets/500newsgoldstandard.xml"

			CorpusXmlReader reader = new CorpusXmlReader(new File(TestFile));
			Corpus corpus = reader.getCorpus();
			log.info("Corpus size: " + corpus.getNumberOfDocuments());

			DisambiguationAlgorithm algo = new NEDAlgo_HITS(dataDirectory, nodeType, edgeType);
			// DisambiguationAlgorithm algo = new NEDAIDADisambiguator();
			// DisambiguationAlgorithm algo = new NEDSpotlightPoster();

			for (int maxDepth = 1; maxDepth <= 3; ++maxDepth) {
				BufferedWriter bw = new BufferedWriter(new FileWriter("MichaTest_" + TestFile.replace("datasets/", "") + "_" + maxDepth + "_20Dez2013.txt", true));
				bw.write("input: " + TestFile + "\n");

				algo.setMaxDepth(maxDepth);
				for (double threshholdTrigram = 1; threshholdTrigram > 0.5; threshholdTrigram -= 0.01) {
					algo.setThreshholdTrigram(threshholdTrigram);

					Evaluator ev = new Evaluator(languageTag, corpus, algo);
					ev.fmeasure();
					ev.writeFmeasureToFile(bw);

					System.gc();
				}
				bw.close();
			}
			algo.close();
		}
	}

}