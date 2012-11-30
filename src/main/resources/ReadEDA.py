from edatoolkit import qLogFile
import os, sys

rpf = 5000 # read per file

edafile = qLogFile(sys.argv[1])
eda = edafile.EDA()
acc_x = edafile.ACC_X()
acc_y = edafile.ACC_Y()
acc_z = edafile.ACC_Z()
temp = edafile.Temperature()

# assume the lengths are the same
num_reads = len(eda)
num_files = num_reads/read_per_file

index = 0
while index < num_reads:
	limit = index+rpf if index+rpf < num_reads else num_reads-1
	eda_c = eda[index:limit]
	acc_x_c = acc_x[index:limit]
	acc_y_c = acc_y[index:limit]
	acc_z_c = acc_z[index:limit]
	temp_c = temp[index:limit]
	f_name = 
	index = index + rpf
print "|".join([str(x) for x in edafile.EDA()]) #Access list of float values for EDA
print "|".join([str(x) for x in edafile.ACC_X()]) #Access list of float values for X
print "|".join([str(x) for x in edafile.ACC_Y()]) #Access list of float values for Y
print "|".join([str(x) for x in edafile.ACC_Z()]) #Access list of float values for Z
print "|".join([str(x) for x in edafile.Temperature()]) #Access list of float values for Temperature

print "Start: " + edafile.startTime.isoformat(" ")
print "End: " + edafile.endTime.isoformat(" ")

edafile.save("Copy.eda")
