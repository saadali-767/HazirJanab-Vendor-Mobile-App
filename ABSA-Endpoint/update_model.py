from flask import Flask, request, jsonify
import pandas as pd
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.multioutput import MultiOutputClassifier
from sklearn.model_selection import train_test_split
import os

app = Flask(__name__)

# File paths
DATASET_PATH = '24dataset.csv'
MODEL_PATH = 'multioutput_classifier.pkl'
VECTORIZER_PATH = 'tfidf_vectorizer.pkl'

# Load initial models and vectorizer
if os.path.exists(VECTORIZER_PATH) and os.path.exists(MODEL_PATH):
    with open(VECTORIZER_PATH, 'rb') as f:
        tfidf_vectorizer = pickle.load(f)
    with open(MODEL_PATH, 'rb') as f:
        multi_output_classifier = pickle.load(f)
else:
    # Initialize if not existing
    tfidf_vectorizer = TfidfVectorizer()
    multi_output_classifier = MultiOutputClassifier(RandomForestClassifier(random_state=42))

# Function to preprocess text
def preprocess_text(text):
    # Your text preprocessing steps
    return text.lower()  # Add your actual preprocessing steps here

@app.route("/update_model", methods=['POST'])
def update_model():
    if request.is_json:
        data = request.get_json()
        review_text = data.get('text', '')

        if not review_text:
            return jsonify({"error": "No text provided"}), 400

        # Predict scores using the existing model
        processed_text = preprocess_text(review_text)
        vectorized_text = tfidf_vectorizer.transform([processed_text])
        predicted_scores = multi_output_classifier.predict(vectorized_text)[0]

        # Append new review and predicted scores to dataset
        new_data = {
            'Review_Text': review_text,
            'Timeliness': predicted_scores[0],
            'Quality_of_Service': predicted_scores[1],
            'Expertise_and_Knowledge': predicted_scores[2]
        }
        df_new = pd.DataFrame(new_data, index=[0])
        if os.path.exists(DATASET_PATH):
            df = pd.read_csv(DATASET_PATH, encoding='cp1252')
            df = pd.concat([df, df_new], ignore_index=True)
        else:
            df = df_new
        df.to_csv(DATASET_PATH, index=False)

        # Retrain the model with updated dataset
        X = tfidf_vectorizer.fit_transform(df['Review_Text'])
        y = df[['Timeliness', 'Quality_of_Service', 'Expertise_and_Knowledge']]
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        multi_output_classifier.fit(X_train, y_train)

        # Save the retrained model and vectorizer
        with open(MODEL_PATH, 'wb') as file:
            pickle.dump(multi_output_classifier, file)
        with open(VECTORIZER_PATH, 'wb') as file:
            pickle.dump(tfidf_vectorizer, file)

        return jsonify({"message": "Model updated successfully"}), 200
    else:
        return jsonify({"error": "Request must be JSON"}), 400

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0' , port=5001)
