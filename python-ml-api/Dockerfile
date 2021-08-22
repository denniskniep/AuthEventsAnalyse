#FROM python:3.6.14-alpine
FROM jupyter/datascience-notebook:notebook-6.1.4

WORKDIR /program

COPY requirements.txt .

RUN pip3 install -r requirements.txt

COPY src/ .

CMD ["python3", "./app.py"]