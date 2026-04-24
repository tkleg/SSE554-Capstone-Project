import re

#Filters out lines we do not want in the graph
def my_filter(line):
    #Filter out lines that include the data_manipulation package or the root package
    if "data_manipulation" in line or "\"org.troy.capstone\"" in line:
        return False
    
    #Filter out the ItemBuilder class, as this is lombok generated
    if "ItemBuilder" in line:
        return False
    
    #Do not allow the SortingAnalysis and MyBM25 classes, as they are not part of main execution
    if "SortingAnalysis" in line or "MyBM25" in line:
        return False
    
    #Metadata linses that are needed for the graph to be valid
    if line.startswith("digraph") or line.startswith("}") or "// Path:" in line:
        return True
    
    #Only allow lines between two custom classes
    if line.count("org.troy") == 2:
        return True
    
    return False


with open("docs/dependency_graph/classes.dot") as f:
    lines = f.readlines()
lines = list(filter(my_filter, lines))
#Get rid of the " (classes)" suffix that jdeps adds to the end of every package name to make the graph easier to read
lines = list(map(lambda x: x.replace(' (classes)', ''), lines))
#Get rid of the root package name from the front of every package name to make the graph easier to read
lines = list(map(lambda x: x.replace('org.troy.capstone.', ''), lines))
#Get rid of $ signs with numbers to the right
lines = list(map(lambda x: re.sub(r'\$\d+', '', x), lines))
#Get rid of duplicates
new_lines = []
pair_set = set()
for line in lines:
    match = re.search(r'"([^"]+)".*->.*"([^"]+)"', line)
    if match:
        pair = (match.group(1), match.group(2))
        if pair not in pair_set:
            new_lines.append(line)
            pair_set.add(pair)
    else:
        new_lines.append(line)
lines = new_lines
#Get rid of self loops by getting strings in first two sets of quotes and checking if they are the same
#Also, get rid of lines who have any group with no period in it, as those are root package classes
re_str = "\"([^\"]*)\".*->.*\"([^\"]*)\""
new_lines = []
for line in lines:
    match = re.search(re_str, line)
    if match:
        if match.re.groups == 2 and match.group(1) != match.group(2) and "." in match.group(1) and "." in match.group(2):
            new_lines.append(line)
    else:
        new_lines.append(line)
lines = new_lines
 # Get rid of connections between classes and their own inner classes
re_str = r'"([^"]+)"\s*->\s*"([^"]+)"'
new_lines = []
for line in lines:
    match = re.search(re_str, line)
    if match:
        class_no_outer_1 = match.group(1).split('$')[0]
        class_no_outer_2 = match.group(2).split('$')[0]
        if class_no_outer_1 == class_no_outer_2:
            continue
    new_lines.append(line)
lines = new_lines
with open("docs/dependency_graph/filtered_classes.dot", "w") as f:
    f.writelines(lines)

#Remove lines using utils, enums, and annotations to clean up the graph, as these are not important for understanding the main execution flow
new_lines = []
for line in lines:
    if "constants" in line or "utils" in line or "annotations" in line:
        continue
    new_lines.append(line)
with open("docs/dependency_graph/filtered_cleaner_classes.dot", "w") as f:
    f.writelines(new_lines)