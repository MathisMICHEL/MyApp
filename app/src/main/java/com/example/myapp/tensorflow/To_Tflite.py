import tensorflow as tf
import os

saved_model_dir = os.path.join(os.getcwd(), "model", "mobilenet-v2-tensorflow2-035-128-classification-v2")
tflite_model_path = os.path.join(os.getcwd(), "model", "mobilenet_v2.tflite")

if not os.path.exists(saved_model_dir):
    raise FileNotFoundError(f"SavedModel directory not found: {saved_model_dir}")

converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
tflite_model = converter.convert()

os.makedirs(os.path.dirname(tflite_model_path), exist_ok=True)
with open(tflite_model_path, 'wb') as f:
    f.write(tflite_model)

print(f"TensorFlow Lite model saved at: {tflite_model_path}")