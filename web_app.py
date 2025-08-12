from flask import Flask, render_template, request
import subprocess
import os
from tcp_client import tcpclient
import time

app = Flask(__name__)

# Start the EDL server in the background
edl_server_process = None

def start_edl_server():
    global edl_server_process
    if edl_server_process is None or edl_server_process.poll() is not None:
        print("Starting EDL server...")
        # Using ./edl assumes it's in the same directory and executable
        edl_server_process = subprocess.Popen(["./edl", "server"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        time.sleep(2) # Give the server a moment to start
        print("EDL server started.")

@app.route('/')
def index():
    return render_template('index.html', output=None)

@app.route('/run', methods=['POST'])
def run_command():
    command = request.form['command']

    # Ensure the EDL server is running
    start_edl_server()

    output = ""
    try:
        # Connect to the EDL server and send the command
        client = tcpclient(1340)
        output = client.sendcommands([command])
    except Exception as e:
        output = f"Error: {e}"

    return render_template('index.html', output=output)

if __name__ == '__main__':
    start_edl_server()
    app.run(host='0.0.0.0', port=5000)
