from FA import FA

if __name__ == '__main__':
    fa = FA("fa.txt")
    print(fa)
    if fa.isDeterministic():
        seq = input("Enter a sequence: ")
        print(fa.isAccepted(seq))
