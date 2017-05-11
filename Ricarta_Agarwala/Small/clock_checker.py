import sys
import traceback


def check_concurrency(clock_values):

    result, less, great = False, False, False
    first, second = clock_values[0], clock_values[1]

    if len(first) != len(second):
        return True

    for k in range(num_proc):
        if first[k] < second[k]:
            less = True
        if first[k] > second[k]:
            great = True

        if less and great:
            result = True
            break

    return result

if __name__ == "__main__":

    if len(sys.argv) != 2:
        print "Wrong number of arguments"
        sys.exit(-1)

    num_proc = int(sys.argv[1])
    res, vec, prev, clockFile = True, [], [], "clock.txt"

    print "Number of Processes {0}".format(num_proc)
    vec.append([0 for i in range(num_proc)])
    try:
        cf = open(clockFile, 'r')
        for line in cf:
            temp = line
            temp = temp.replace('[','')
            temp = temp.replace(']', '')
            vec.append([int(token) for token in temp.split(',')])

            if check_concurrency(vec):
                print "{0} and {1} are concurrent".format(vec[0], vec[1])
                res = False

            del vec[0]

        print "Result: {0}".format("PASS" if res else "FAILED")
    except IOError:
        traceback.print_exc()
