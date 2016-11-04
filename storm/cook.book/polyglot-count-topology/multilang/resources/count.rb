require "./storm"

class CountBolt < Storm::Bolt
  attr_accessor :counts
  def initialize
    @counts = Hash.new
  end
  
  def process(tup)
    word = String(tup.values[0])
    counts[:word] = counts[:word].to_i + 1.to_i
    emit([word, counts[:word].to_s])  
  end
end

CountBolt.new.run