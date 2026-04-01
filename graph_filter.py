# Filters out lines not of the graph overhead or between two custom classes
def my_filter(line):
    if line.startswith("digraph"):
        return True
    if line.startswith("}"):
        return True
    if "// Path:" in line:
        return True
    if line.count("org.troy") >= 2:
        return True
    if "data_manipulation" in line:
        return False
    
    return False


with open("deps/classes.dot") as f:
    lines = f.readlines()
    lines = list(filter(my_filter, lines))
    lines = list(map(lambda x: x.replace(' (classes)', ''), lines))
    lines = list(map(lambda x: x.replace('org.troy.capstone.', ''), lines))
with open("deps/filtered_classes.dot", "w") as f:
    f.writelines(lines)