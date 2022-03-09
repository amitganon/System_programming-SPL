import sys
import os
import zipfile
from pathlib import Path
import shutil

def get_students_ids(file_name):
    file_parts = file_name.split('/')
    file_name = file_parts[-1]
    file_name = file_name[:-4]
    students = file_name.split('_')
    print("Students:",students)
    return students


#To run this use the following arguments:
#python3 test_assignment.py ID1_ID2.zip config.txt orders.txt true_output.txt true_database.db
#It will create a folder with the name ID1_ID2 and extract the zip file to that folder,
#then run the code and compare the output with the true output and output the grade
#It will delete the folder after it is done.
#Make sure the IDs outputted (in Students: []) are correct, as they will be used to assign the grade.
if __name__ == '__main__':
    file_name = sys.argv[1]
    if not file_name.endswith(".zip"):
        print("File name should end with .zip")
        sys.exit(1)
    students = get_students_ids(file_name)
    dir_name = '_'.join(students)
    Path(dir_name).mkdir(parents=True, exist_ok=True) 
    try:
        zip = zipfile.ZipFile(file_name, "r")
        zip.extractall(dir_name)
        zip.close()
    except Exception as e:
        print(e)
        print("Failed to extract zip file")
        sys.exit(1)
    dir_contents = os.listdir(dir_name)
    if "main.py" not in dir_contents:
        print("No main.py file in the submission")
        sys.exit(1)
    main_path = os.path.join(dir_name, "main.py")
    output_file = os.path.join(dir_name, "output.txt")
    db_file = os.path.join(dir_name, "database.db")
    print(f'python3 {main_path} {sys.argv[2]} {sys.argv[3]} {output_file} {db_file}')
    os.system(f'python3 {main_path} {sys.argv[2]} {sys.argv[3]} {output_file} {db_file}')
    os.system(f'python3 compare_output.py {output_file} {sys.argv[4]} {db_file} {sys.argv[5]}')
    shutil.rmtree(dir_name)




