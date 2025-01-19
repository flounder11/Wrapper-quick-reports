FROM postgres:15
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=fastrep_db
EXPOSE 5434
CMD ["postgres", "-c", "port=5434"]

