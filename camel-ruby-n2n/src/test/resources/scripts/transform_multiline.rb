# Get the input body and remove any trailing whitespace
input = $body.to_s.strip

# Add processing metadata
set_header('ProcessedBy', 'RubyScript')
set_header('ProcessingTime', Time.now.to_s)

# Transform the data
transformed = input.upcase

# Add metadata
set_exchange_property('originalLength', input.length)
set_exchange_property('transformedLength', transformed.length)

# Set status based on length
set_header('Status', transformed.length > 100 ? 'LARGE_MESSAGE' : 'NORMAL')

# Return transformed data
transformed