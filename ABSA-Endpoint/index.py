from flask import Flask, request, jsonify
import numpy as np
import pickle
import nltk
from sklearn.feature_extraction.text import TfidfVectorizer
import re
from nltk.tokenize import word_tokenize
from nltk.stem import WordNetLemmatizer
from spellchecker import SpellChecker

app = Flask(__name__)

# Load models
tfidf_vectorizer = pickle.load(open("tfidf_vectorizer.pkl", "rb"))
multi_output_classifier = pickle.load(open("multioutput_classifier.pkl", "rb"))

# Preprocessing function
def preprocess_text(text):
    # Lowercasing
    text = text.lower()

    # Removing punctuation
    text = re.sub(r'[^\w\s]', '', text)

    # Tokenization
    words = word_tokenize(text)

    # Spell check and lemmatization
    spell = SpellChecker()
    lemmatizer = WordNetLemmatizer()
    processed_words = []

    for word in words:
        # Apply spell correction
        corrected_word = spell.correction(word)

        # Apply lemmatization
        lemmatized_word = lemmatizer.lemmatize(corrected_word)
        processed_words.append(lemmatized_word)

    return ' '.join(processed_words)

# Function to vectorize text
def vectorize_text(text, vectorizer):
    # Preprocess the text
    preprocessed_text = preprocess_text(text)

    # Vectorize the text
    return vectorizer.transform([preprocessed_text])

@app.route("/predict", methods=['POST'])
def predict():
    if request.is_json:
        data = request.get_json()
        text = data.get('text', '')

        if not text:
            return jsonify({"error": "No text provided"}), 400

        vectorized_text = vectorize_text(text, tfidf_vectorizer)

        predictions = multi_output_classifier.predict(vectorized_text)[0]

        response = {
            'Timeliness': int(predictions[0]),
            'Quality_of_Service': int(predictions[1]),
            'Expertise_and_Knowledge': int(predictions[2])
        }
        return jsonify(response)
    else:
        return jsonify({"error": "Request must be JSON"}), 400

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0')
