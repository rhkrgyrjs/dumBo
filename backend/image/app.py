from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
import os
import uuid
from PIL import Image  # 추가
import io

app = Flask(__name__)
CORS(app, origins=["http://localhost:3000"])

UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def is_valid_image(file_stream):
    try:
        image = Image.open(file_stream)
        image.verify()  # 이미지 손상 여부 확인
        file_stream.seek(0)  # 스트림 위치 초기화 (중요!)
        return True
    except Exception:
        return False

@app.route('/upload', methods=['POST'])
def upload_image():
    if 'image' not in request.files:
        return jsonify({"error": "No image part"}), 400

    file = request.files['image']
    print("Received filename:", file.filename)

    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file and allowed_file(file.filename):
        # 파일 시그니처 검사
        if not is_valid_image(file.stream):
            return jsonify({"error": "Invalid image file"}), 400

        ext = os.path.splitext(file.filename)[1].lower()  # 확장자만 추출(.jpg 등)
        unique_name = f"{uuid.uuid4().hex}{ext}"  # UUID + 확장자

        save_path = os.path.join(app.config['UPLOAD_FOLDER'], unique_name)
        file.seek(0)  # 저장 전에 스트림 위치 초기화 필수
        file.save(save_path)

        image_url = f"http://localhost:5000/uploads/{unique_name}"
        return jsonify({"url": image_url})

    return jsonify({"error": "File type not allowed"}), 400

@app.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

# --------------------
# 새로 추가할 이미지 삭제 API
# --------------------
@app.route('/delete-images', methods=['POST'])
def delete_images():
    data = request.get_json()
    print(data)
    if not data or 'filenames' not in data:
        return jsonify({"error": "Missing 'filenames' in request body"}), 400

    filenames = data['filenames']
    if not isinstance(filenames, list):
        return jsonify({"error": "'filenames' must be a list"}), 400

    deleted = []
    not_found = []

    for filename in filenames:
        # 안전하게 경로 조합
        file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        # 디렉터리 탈출 공격 방지
        if not os.path.abspath(file_path).startswith(os.path.abspath(app.config['UPLOAD_FOLDER'])):
            continue

        if os.path.exists(file_path):
            try:
                os.remove(file_path)
                deleted.append(filename)
            except Exception as e:
                # 삭제 실패해도 무시하거나 로그 남기기
                print(f"Error deleting {filename}: {e}")
        else:
            not_found.append(filename)

    return jsonify({
        "deleted": deleted,
        "not_found": not_found
    })

if __name__ == "__main__":
    app.run(port=5000)
