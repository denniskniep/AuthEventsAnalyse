FROM jupyter/datascience-notebook:notebook-6.1.4
ADD  ./requirements.txt /tmp

RUN jupyter labextension install jupyterlab-plotly
RUN jupyter labextension install jupyterlab-chart-editor
#RUN jupyter labextension install @jupyterlab/dataregistry-extension

RUN  python3 -m pip install -r /tmp/requirements.txt
RUN  python3 -m jupyter nbextension enable --py --sys-prefix qgrid
RUN  python3 -m jupyter nbextension enable --py widgetsnbextension


#RUN  python3 -m pip install papermill


