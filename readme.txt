In the configuration file `application.conf`:
`file.reader.filepath` should point on the file to process

`data.processor.user.uuids.user.first` - uuid of the first user
`data.processor.user.uuids.user.second` - uuid of the second user

`data.processor.meeting.precision.maxLength` - max distance between persons to be met
`data.processor.meeting.precision.maxTime` - max time difference between movements traces to be met

1. Vanilla scala: scala.io supports processing of big files.
2. Complexity:
   find floor O(1) + find all elements on the floor O(N).

   Considering the fact that there are M records of person 1 and N records of Person 2:
   time complexity is: M + M*N
3.
    a) if large batch of queries mean to support the search of multiple uuids instead of pair, we will need to adjust configuration in configuration manager
    b) implement http layer
    c) infinite memory
