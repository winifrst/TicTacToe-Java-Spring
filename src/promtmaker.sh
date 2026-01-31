#!/bin/bash

# Имя выходного файла
OUTPUT_FILE="all_java_files.txt"

# Очищаем файл, если он уже существует
> "$OUTPUT_FILE"

# Поиск всех .java файлов и запись их содержимого в OUTPUT_FILE
find . -type f -name "*.java" -exec cat {} >> "$OUTPUT_FILE" \;

echo "Все .java файлы скопированы в $OUTPUT_FILE"
