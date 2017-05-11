import sys
import traceback


class Checker(object):

    configName = "config.txt"
    num_nodes, num_request, result = -1, -1, True
    req_satisfied_count = {}

    def __init__(self, configName):
        super(Checker, self).__init__()
        self.configName = configName

    def parse(self):
        try:
            cfg = open(self.configName, "r")
            for input_line in cfg:

                if input_line.startswith("#"):
                    continue

                if '#' in input_line:
                    input_line = input_line[:input_line.find('#')]
                line = input_line.strip()

                if len(line) == 0:
                    continue

                # Extracting number of nodes and request
                self.num_nodes = line.split(' ')[0].strip()
                self.num_request = line.split(' ')[3].strip()
                print "Number of nodes = {0}\nNumber of Requests = {1}"\
                    .format(self.num_nodes, self.num_request)

                break

            # Initialize Dictionary
            for i in range(int(self.num_nodes)):
                self.req_satisfied_count[i] = 0

        except IOError:
            traceback.print_exc()
            sys.exit(-1)
        except ValueError:
            traceback.print_exc()
            sys.exit(-1)

    def start_check(self):
        shared_file = "SharedResource.txt"
        line_num, current_node, string = 0, -1, ""

        try:
            shared = open(shared_file, "r")

            for input_line in shared:
                input_line = input_line.strip()

                if len(input_line) == 0:
                    continue
                    
                if line_num % 4 == 0:
                    current_node = input_line.split(' ')[0].strip()
                    string = current_node + " in CS."

                line_num += 1

                if input_line != string:
                    self.result = False
                    self.print_stats(line_num)
                    sys.exit(-1)

                if line_num % 4 == 0:
                    self.req_satisfied_count[int(current_node)] += 1
                    current_node = -1
                    string = ""

            # Check if the number of request are satisfied for each node
            for j in range(int(self.num_nodes)):
                if self.req_satisfied_count.get(j) != int(self.num_request):
                    self.result = False
                    break

            # If program reaches here, it worked
            self.print_stats(line_num)
        except IOError:
            traceback.print_exc()
            sys.exit(-1)

    def print_stats(self, line_num):
        print "*" * 40
        print "Result: {0}".format("PASS" if self.result else "FAILED")
        print "*" * 40

        if not self.result:
            print "Failed at line : {0}".format(line_num)

        print ""

        for i in range( int(self.num_nodes) ):
            print "Request satisfied for Node-{0}: {1}".\
                format(i, self.req_satisfied_count.get(i))

        print "*" * 40

if __name__ == "__main__":

    if len(sys.argv) != 2:
        print "Wrong number of arguments"
        sys.exit(-1)

    mutex_checker = Checker( str(sys.argv[1]))
    mutex_checker.parse()   # Parse the config file
    if mutex_checker.num_nodes == -1 or mutex_checker.num_request == -1:
        print "Error in parsing config file."
        sys.exit(-1)

    mutex_checker.start_check() # Start mutex checker