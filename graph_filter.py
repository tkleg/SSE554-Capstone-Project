#Filters out lines we do not want in the graph
def my_filter(line):
    #Filter out lines that include the data_manipulation package or the root package
    if "data_manipulation" in line or "\"org.troy.capstone\"" in line:
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
with open("docs/dependency_graph/filtered_classes.dot", "w") as f:
    f.writelines(lines)