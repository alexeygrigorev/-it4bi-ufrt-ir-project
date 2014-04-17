package it4bi.ufrt.ir.service.dw.ner;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class LazyClassifier {

	private static final String DEFAULT_SERIALIZED_CLASSIFIER = 
			"edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";
	private static final Logger LOGGER = LoggerFactory.getLogger(LazyClassifier.class);

	private final LazyInitializer<CRFClassifier<CoreLabel>> lazyLoader;

	public LazyClassifier(final String path) {
		this.lazyLoader = new LazyInitializer<CRFClassifier<CoreLabel>>() {
			@Override
			protected CRFClassifier<CoreLabel> initialize() throws ConcurrentException {
				try {
					LOGGER.debug("initializing classifier from {}", path);
					return CRFClassifier.getClassifier(path);
				} catch (ClassCastException | ClassNotFoundException | IOException e) {
					throw new ConcurrentException(e);
				}
			}
		};
	}

	public static LazyClassifier defaultClassifier() {
		return new LazyClassifier(DEFAULT_SERIALIZED_CLASSIFIER);
	}

	public List<List<CoreLabel>> classify(String query) {
		return init().classify(query);
	}

	public CRFClassifier<CoreLabel> init() {
		try {
			return lazyLoader.get();
		} catch (ConcurrentException e) {
			throw Throwables.propagate(e);
		}
	}

}
